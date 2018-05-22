# -*- coding: utf-8 -*-
from scrapy.spider import Spider
from scrapy.contrib.spiders import CrawlSpider, Rule
#from scrapy.contrib.linkextractors.sgml import SgmlLinkExtractor
from scrapy.linkextractors import LinkExtractor
from scrapy.selector import Selector
from scrapy.http import Request, HtmlResponse
from scrapy import log

#from items import BDzdItem


class BDzdSpider(CrawlSpider):
    global qa_number;
    qa_number=0;
    """爬取百度知道 银行"""
    log.msg("log",level=log.DEBUG)
    def _requests_to_follow(self, response):
        if not isinstance(response, HtmlResponse):
            return
        seen = set()
        for n, rule in enumerate(self._rules):
            links = [lnk for lnk in rule.link_extractor.extract_links(response)
                     if lnk not in seen]
            if links and rule.process_links:
                links = rule.process_links(links)
            for link in links:
                if link.text.find("银行") == -1:
                    continue;
                seen.add(link)
                r = Request(url=link.url, callback=self._response_downloaded)
                r.meta.update(rule=n, link_text=link.text)
                yield rule.process_request(r)

    name = "bankSpider"


    download_delay = 1
    allowed_domains = ["zhidao.baidu.com"]
    start_urls = [
        "https://zhidao.baidu.com/question/1796062605517856547.html?fr=iks&word=%D2%F8%D0%D0&ie=gbk"
    ]

    rules = [
        Rule(LinkExtractor(allow=('/question/.*'),
                               restrict_xpaths=('//a[@class="related-link"]')),
             callback='parse_item',
             follow=True)
    ]

    def parse_item(self, response):
        #return;
       # open("aa.txt", 'wb').write(response.body)
        sel = Selector(response)
        url=response._url;
        question=sel.xpath('//span[@class="ask-title "]/text()').extract()
        answer = sel.xpath('//pre[@class="best-text mb-10"]/text()').extract()
        otherAnswer=sel.xpath('//div[@class="answer-text line"]/span/text()').extract()
        #sites=sel.xpath('//a[@class="related-link"]')

        item = BDzdItem()
        item["question"] = ''.join(question);
        if len(answer) > 0:
            item["answer"] = ''.join(answer);#因为xpath text()截出来可能是字符数组，要转成字符
        elif len(otherAnswer) > 0:
            item["answer"] = ''.join(otherAnswer[0]);
        else:
            return;

        global qa_number
        qa_number=qa_number+1;
        item["number"]=qa_number
        print "@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@ 第" + str(qa_number)+" 条";
        print "@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@" + url;
        print "##########################################" + item["question"];
        print "*******************************************" +  item["answer"];

        yield item