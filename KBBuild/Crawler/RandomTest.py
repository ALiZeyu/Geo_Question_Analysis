#coding:utf-8
import urllib, urllib2
from bs4 import BeautifulSoup
import bs4
import codecs
import sys, getopt
import pre_work
reload(sys)
sys.setdefaultencoding('utf-8')
import urllib2
from urllib2 import quote
import re
from selenium import webdriver
import os

class CCC:
    my_list = []
    def __init__(self, v):
        print("create new instance")
        self.my_list.append(v)

def test_CCC():
    value = ['1', '2', '3']
    for v in value:
        temp = CCC(v)
        # temp.my_list.append(v)
        print len(temp.my_list)


def get_contents(word):
    #前半部分的链接(注意是http，不是https)
    url_pre = 'http://www.baidu.com/s'

    #GET参数
    params = {}
    params['pn'] = 20 #设置这个每页可以获取10个内容

    #若是unicode编码，转成str
    if isinstance(word, unicode):
        params['wd'] = word.encode('utf-8')
    url_params = urllib.urlencode(params)

    #GET请求完整链接
    #url = '%s?%s' % (url_pre, url_params)
    url = u'https://www.baidu.com/baidu?wd='+quote(word)+'&tn=monline_dg&ie=utf-8'

    #打开链接，获取响应
    request = urllib2.Request(url)
    response = urllib2.urlopen(request)

    #获取响应的html
    html = response.read()

    soup = BeautifulSoup(html, 'lxml')
    title_list = soup.find_all(re.compile("(div|span)"),{'class':"c-tools"})
    for node in title_list:
        if 'data-tools' in node.attrs:
            print node['data-tools']

    #解析内容得到右侧相关实体
    # soup = BeautifulSoup(html, 'lxml')
    # dl_list = soup.find_all('dl')
    # entity_list = []
    # xiangguan = soup.find_all('div',{'class': re.compile("(cr-title c-clearfix|^c-span4)")})
    # for x in xiangguan:
    #     print x
    #     if x['class'][0].startswith('c-span4'):
    #         hehe = eval(x['data-click'])
    #         entity_list.append(hehe['rsv_re_ename'])
    #     else:
    #         entity_title = x.find(lambda tag: tag.name == 'span'and 'title' in tag.attrs)
    #         if entity_title is not None:
    #             entity_list.append(entity_title['title'])
    # for x in entity_list:
    #     print x
    # items = soup.select('.t a')

    #写入文件
    # f = codecs.open('test.txt', 'w', 'utf-8')
    # for item in items:
    #     f.write('%s\r\n' % ''.join(item.stripped_strings))
    #     f.write('%s\r\n\r\n' % item['href'])
    # f.close()

def write_page():
    # http://www.w3school.com.cn/rdf/rdf_rules.asp
    url = 'https://baike.baidu.com/item/%E7%AC%AC%E5%9B%9B%E7%BA%AA%E5%86%B0%E6%9C%9F/5329536'
    request = urllib2.Request(url)
    response = urllib2.urlopen(request)

    #获取响应的html
    html = response.read()

    #解析内容
    soup = BeautifulSoup(html, 'lxml')
    # response = urllib2.urlopen(url)
    # html = response.read().decode('UTF-8').encode('utf-8')
    # with open('test.txt', 'w') as f:
    #     f.write(html)

def bs_test():
    html = """
    <div id="page" >


	    <a href="/s?wd=python%20list.sort%20key&pn=0&oq=python%20list.sort%20key&tn=SE_PSStatistics_p1d9m0nf&ie=utf-8&usm=1&rsv_pq=94231db1000122f7&rsv_t=c9adXnSZ3p%2FF6Kjk2DvxuzXR3rzM0P8wzAueeJmtYAcK5B2HO%2Ft5qe11vVGFJldrKnYGG0XUAc7R8BW1tWAG&rsv_page=-1" class="n">&lt;上一页</a><a href="/s?wd=python%20list.sort%20key&pn=0&oq=python%20list.sort%20key&tn=SE_PSStatistics_p1d9m0nf&ie=utf-8&usm=1&rsv_pq=94231db1000122f7&rsv_t=c9adXnSZ3p%2FF6Kjk2DvxuzXR3rzM0P8wzAueeJmtYAcK5B2HO%2Ft5qe11vVGFJldrKnYGG0XUAc7R8BW1tWAG"><span class="fk"><i class="c-icon c-icon-bear-pn"></i></span><span class="pc">1</span></a><strong><span class="fk fk_cur"><i class="c-icon c-icon-bear-p"></i></span><span class="pc">2</span></strong><a href="/s?wd=python%20list.sort%20key&pn=20&oq=python%20list.sort%20key&tn=SE_PSStatistics_p1d9m0nf&ie=utf-8&usm=1&rsv_pq=94231db1000122f7&rsv_t=c9adXnSZ3p%2FF6Kjk2DvxuzXR3rzM0P8wzAueeJmtYAcK5B2HO%2Ft5qe11vVGFJldrKnYGG0XUAc7R8BW1tWAG"><span class="fk"><i class="c-icon c-icon-bear-pn"></i></span><span class="pc">3</span></a><a href="/s?wd=python%20list.sort%20key&pn=30&oq=python%20list.sort%20key&tn=SE_PSStatistics_p1d9m0nf&ie=utf-8&usm=1&rsv_pq=94231db1000122f7&rsv_t=c9adXnSZ3p%2FF6Kjk2DvxuzXR3rzM0P8wzAueeJmtYAcK5B2HO%2Ft5qe11vVGFJldrKnYGG0XUAc7R8BW1tWAG"><span class="fk fkd"><i class="c-icon c-icon-bear-pn"></i></span><span class="pc">4</span></a><a href="/s?wd=python%20list.sort%20key&pn=40&oq=python%20list.sort%20key&tn=SE_PSStatistics_p1d9m0nf&ie=utf-8&usm=1&rsv_pq=94231db1000122f7&rsv_t=c9adXnSZ3p%2FF6Kjk2DvxuzXR3rzM0P8wzAueeJmtYAcK5B2HO%2Ft5qe11vVGFJldrKnYGG0XUAc7R8BW1tWAG"><span class="fk"><i class="c-icon c-icon-bear-pn"></i></span><span class="pc">5</span></a><a href="/s?wd=python%20list.sort%20key&pn=50&oq=python%20list.sort%20key&tn=SE_PSStatistics_p1d9m0nf&ie=utf-8&usm=1&rsv_pq=94231db1000122f7&rsv_t=c9adXnSZ3p%2FF6Kjk2DvxuzXR3rzM0P8wzAueeJmtYAcK5B2HO%2Ft5qe11vVGFJldrKnYGG0XUAc7R8BW1tWAG"><span class="fk fkd"><i class="c-icon c-icon-bear-pn"></i></span><span class="pc">6</span></a><a href="/s?wd=python%20list.sort%20key&pn=60&oq=python%20list.sort%20key&tn=SE_PSStatistics_p1d9m0nf&ie=utf-8&usm=1&rsv_pq=94231db1000122f7&rsv_t=c9adXnSZ3p%2FF6Kjk2DvxuzXR3rzM0P8wzAueeJmtYAcK5B2HO%2Ft5qe11vVGFJldrKnYGG0XUAc7R8BW1tWAG"><span class="fk"><i class="c-icon c-icon-bear-pn"></i></span><span class="pc">7</span></a><a href="/s?wd=python%20list.sort%20key&pn=70&oq=python%20list.sort%20key&tn=SE_PSStatistics_p1d9m0nf&ie=utf-8&usm=1&rsv_pq=94231db1000122f7&rsv_t=c9adXnSZ3p%2FF6Kjk2DvxuzXR3rzM0P8wzAueeJmtYAcK5B2HO%2Ft5qe11vVGFJldrKnYGG0XUAc7R8BW1tWAG"><span class="fk fkd"><i class="c-icon c-icon-bear-pn"></i></span><span class="pc">8</span></a><a href="/s?wd=python%20list.sort%20key&pn=80&oq=python%20list.sort%20key&tn=SE_PSStatistics_p1d9m0nf&ie=utf-8&usm=1&rsv_pq=94231db1000122f7&rsv_t=c9adXnSZ3p%2FF6Kjk2DvxuzXR3rzM0P8wzAueeJmtYAcK5B2HO%2Ft5qe11vVGFJldrKnYGG0XUAc7R8BW1tWAG"><span class="fk"><i class="c-icon c-icon-bear-pn"></i></span><span class="pc">9</span></a><a href="/s?wd=python%20list.sort%20key&pn=90&oq=python%20list.sort%20key&tn=SE_PSStatistics_p1d9m0nf&ie=utf-8&usm=1&rsv_pq=94231db1000122f7&rsv_t=c9adXnSZ3p%2FF6Kjk2DvxuzXR3rzM0P8wzAueeJmtYAcK5B2HO%2Ft5qe11vVGFJldrKnYGG0XUAc7R8BW1tWAG"><span class="fk fkd"><i class="c-icon c-icon-bear-pn"></i></span><span class="pc">10</span></a><a href="/s?wd=python%20list.sort%20key&pn=20&oq=python%20list.sort%20key&tn=SE_PSStatistics_p1d9m0nf&ie=utf-8&usm=1&rsv_pq=94231db1000122f7&rsv_t=c9adXnSZ3p%2FF6Kjk2DvxuzXR3rzM0P8wzAueeJmtYAcK5B2HO%2Ft5qe11vVGFJldrKnYGG0XUAc7R8BW1tWAG&rsv_page=1" class="n">下一页&gt;</a>


</div>
    """

    #创建Beautiful Soup对象
    soup = BeautifulSoup(html,'lxml')
    dfs(soup)
    # l = []
    # dfs(soup, l)
    # for s in l:
    #     print s


def dfs(node, l):
    if isinstance(node, bs4.element.NavigableString):
        if len(node.string) > 2:
            l.append(node.string)
        return
    for (k,v) in node.attrs.items():
        if len(v) > 2:
            l.append(v)
    for sub in node.contents:
        dfs(sub, l)

def dfs(node):
    if isinstance(node, bs4.element.NavigableString):
        if node.string == u'下一页>':
            print 'got'
        return
    if node.string == u'下一页>':
        print node.attrs['href']
    for sub in node.contents:
        dfs(sub)


def city_extraction(path):
    city_list = []
    f = codecs.open(path, 'r', encoding = 'utf-8')
    soup = BeautifulSoup(f, 'lxml')
    dl_list = soup.find_all('a')
    for dl in dl_list:
        if u'href' in dl.attrs:
            city_list.append(dl.string)
    pre_work.write_file('data/city.txt', city_list)

if __name__ == '__main__':
    print 'jjjjjjjjjjjjjjjj'
    #url = "https://zhidao.baidu.com/question/346920813.html"
    url = 'https://zhidao.baidu.com/question/346920813.html'
    # chromedriver = "C:\Program Files (x86)\Google\Chrome\Application\chromedriver.exe"
    # # 这里的driver就是刚刚上面下载的
    # os.environ["webdriver.chrome.driver"] = chromedriver
    driver = webdriver.Chrome(executable_path="C:\Program Files (x86)\Google\Chrome\Application\chromedriver.exe")
    driver.get(url)
    print (driver.page_source)
    driver.close()
    #test_CCC()
    #get_contents('宁夏回族自治区 省委书记')
    # write_page()
    # bs_test()
    # city_extraction('E://workspace/StanfordDP/data/dic/city.html')