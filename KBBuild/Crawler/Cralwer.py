#!/usr/bin/python
# ^_^ coding:utf8 ^_^

import re
import sys
from urllib import quote
import urllib2
from bs4 import BeautifulSoup
import requests
import bs4
import pre_work
import re
import os

reload(sys)
sys.setdefaultencoding('utf-8')

class crawler:
    '''爬百度搜 索结果的爬虫'''
    url = u''
    urls = []
    keyword = ''
    o_urls = []
    html = ''
    total_pages = 1
    current_page = 0
    next_page_url = ''
    exact_answer = ''  #百度知识图谱返回的答案
    timeout = 60                    #默认超时时间为60秒
    em_word = []
    side_entity = []
    baike_title = ''
    side_flag = False
    headersParameters = {    #发送HTTP请求时的HEAD信息，用于伪装为浏览器
        'Connection': 'Keep-Alive',
        'Accept': 'text/html, application/xhtml+xml, */*',
        'Accept-Language': 'en-US,en;q=0.8,zh-Hans-CN;q=0.5,zh-Hans;q=0.3',
        'Accept-Encoding': 'gzip, deflate',
        'User-Agent': 'Mozilla/6.1 (Windows NT 6.3; WOW64; Trident/7.0; rv:11.0) like Gecko'
    }

    # 生成搜索url
    def __init__(self, keyword):
        self.keyword = keyword.decode('utf-8')
        self.url = u'https://www.baidu.com/baidu?wd='+quote(keyword)+u'&tn=monline_dg&ie=utf-8'
        self.em_word = []
        self.side_entity = []
        self.urls = []
        self.o_urls = []
        self.baike_title = ''

    # 设置超时时间，单位：秒
    def set_timeout(self, time):
        '''设置超时时间，单位：秒'''
        try:
            self.timeout = int(time)
        except:
            pass

    def set_total_pages(self, num):
        '''设置总共要爬取的页数'''
        try:
            self.total_pages = int(num)
        except:
            pass

    def set_current_url(self, url):
        '''设置当前url'''
        self.url = url

    def switch_url(self):
        '''切换当前url为下一页的url
           若下一页为空，则不继续爬取数据
        '''
        if self.next_page_url == '':
            self.current_page = self.total_pages
            #sys.exit()
        else:
            self.set_current_url(self.next_page_url)

    def is_finish(self):
        '''判断是否爬取完毕'''
        if self.current_page >= self.total_pages:
            return True
        else:
            return False

    '''爬取当前url所指页面的内容，保存到html中'''
    def get_html(self):
        r = requests.get(self.url, timeout=self.timeout, headers=self.headersParameters)
        if r.status_code == 200:
            self.html = r.text
            self.current_page += 1
        else:
            self.html = u''
            print '[ERROR]',self.url,u'get此url返回的http状态码不是200'

    '''百度搜索右侧的相关地名等'''
    def get_side_entity(self):
        soup = BeautifulSoup(self.html, 'lxml')
        xiangguan = soup.find_all('div',{'class': re.compile("(cr-title c-clearfix|^c-span4)")})
        for x in xiangguan:
            if x['class'][0].startswith('c-span4'):
                hehe = eval(x['data-click'])
                self.side_entity.append(hehe['rsv_re_ename'])
            else:
                entity_title = x.find(lambda tag: tag.name == 'span'and 'title' in tag.attrs)
                if entity_title is not None:
                    self.side_entity.append(entity_title['title']+'+++')


    '''获取搜索引擎高亮线索词以及知识库答案'''
    def get_emword(self):
        em_set = set()
        en = self.keyword.split()[0]
        attri = self.keyword.split()[1]
        soup = BeautifulSoup(self.html, 'lxml')
        list = soup.find_all('em')
        for node in list:
            # if en in node.string or node.string in en or self.find_lcsubstr(attri, node.string) < 2，有些同义词之间没有重叠字符，如高校和大学
            #关键词不要实体以及这个奇怪的符号
            #if en in node.string or node.string in en or node.string == '...':
            if node.string is not None and (en.find(node.string) != -1 or node.string.find(en) != -1 or node.string == '...'):
                continue
            # 长度大于1
            elif node.string is not None and len(node.string) > 1:
                em_set.add(node.string)
        # 抽取时的关键词最好有一定的顺序，越接近属性本身越好
        self.em_word.extend(em_set)
        self.em_word.sort(key = lambda d:len(d), reverse=True)
        self.em_word.insert(0, attri)
        # 寻找exact_answer and node['class'] == 'op_exactqa_s_answer'
        # div_list = soup.find_all('div')
        # for node in div_list:
        #     if isinstance(node, bs4.element.Tag) and 'class' in node.attrs and 'op_exactqa_s_answer' in node['class']:
        #         answer_node = node.find('a')
        #         if isinstance(answer_node, bs4.element.Tag) and 'href' in answer_node.attrs:
        #             self.exact_answer = answer_node.string.strip()
        # 寻找c-border格式数据 and 'href' in answer_node.attrs
        border = soup.find('div',{'class':"c-border"})
        if border is not None:
            exactqa = border.find('div', {'class' : 'op_exactqa_s_answer'})
            if exactqa is not None:
                answer_node = exactqa.find('a')
                if isinstance(answer_node, bs4.element.Tag) and 'href' in answer_node.attrs:
                    self.exact_answer = answer_node.string.strip()
                elif exactqa.string is not None:
                    self.exact_answer = exactqa.string.strip()
            # exact_answer格式不符合，就看gdp_subtitle
            if self.exact_answer == '':
                gdp = border.find('p', {'class': re.compile('.*subtitle')})
                if gdp is not None and isinstance(gdp, bs4.element.Tag):
                    self.exact_answer = gdp.string.strip()
            if self.exact_answer == '':
                img = border.find_all('p', {'class': 'op_exactqa_item_img'})
                for item in img:
                    item_img = item.find('a')
                    if isinstance(item_img, bs4.element.Tag) and 'title' in item_img.attrs:
                        self.exact_answer += item_img['title'].strip()+ '@@@'
                if self.exact_answer != '':
                    self.exact_answer = self.exact_answer[:-3]
            if self.exact_answer == '':
                # 存在border,那么就应该存在其他格式
                print self.keyword+'new border format'

    '''抽取搜索结果的标题'''
    def get_baike_title(self):
        soup = BeautifulSoup(self.html, 'lxml')
        title_list = soup.find_all(re.compile("(div|span)"), {'class': "c-tools"})
        for node in title_list:
            if 'data-tools' in node.attrs:
                temp_str = node['data-tools'].encode("utf-8")
                title = temp_str[temp_str.index(':')+2:temp_str.index('url')-2]
                if self.baike_title == '' and title.find('_百度百科') != -1:
                    self.baike_title = title[:title.find('_百度百科')]

    def find_lcsubstr(self, s1, s2):
        m=[[0 for i in range(len(s2)+1)]  for j in range(len(s1)+1)]  #生成0矩阵，为方便后续计算，比字符串长度多了一列
        mmax=0   #最长匹配的长度
        p=0  #最长匹配对应在s1中的最后一位
        for i in range(len(s1)):
            for j in range(len(s2)):
                if s1[i]==s2[j]:
                    m[i+1][j+1]=m[i][j]+1
                    if m[i+1][j+1]>mmax:
                        mmax=m[i+1][j+1]
                        p=i+1
        return mmax

    def get_urls(self):
        '''从当前html中解析出搜索结果的url，保存到o_urls'''
        o_urls = re.findall('href\=\"(http\:\/\/www\.baidu\.com\/link\?url\=.*?)"', self.html)
        o_urls = list(set(o_urls))  #去重
        self.o_urls = o_urls
        #取下一页地址
        # next = re.findall(' href\=\"(\/s\?wd\=[\w\d\%\&\=\_\-]*?)\" class\=\"n\"', self.html)
        next = self.get_next_url(self.html)
        if next != '':
            self.next_page_url = 'https://www.baidu.com'+next
        else:
            self.next_page_url = ''


    def get_next_url(self, html):
        soup = BeautifulSoup(html,'lxml')
        # print soup.prettify()
        l = []
        self.dfs(soup, l)
        if len(l) == 1:
            return l[0]
        return ''


    def dfs(self, node, l):
        if isinstance(node, bs4.element.NavigableString):
            # if u'下一页' in node.string:
            #     print 'got'
            return
        if node.string is not None and '下一页' in node.string and 'href' in node.attrs:
             l.append(node.attrs['href'])
        for sub in node.contents:
            self.dfs(sub, l)

    def get_real(self, o_url):
        '''获取重定向url指向的网址'''
        r = requests.get(o_url, allow_redirects = False)    #禁止自动跳转
        if r.status_code == 302:
            try:
                return r.headers['location']    #返回指向的地址
            except:
                pass
        return o_url    #返回源地址


    '''读取当前o_urls中的链接重定向的网址，并保存到urls中'''
    def transformation(self):
        self.urls = []
        for o_url in self.o_urls:
            self.urls.append(self.get_real(o_url))

    def print_urls(self, url_list):
        '''输出当前urls中的url'''
        for url in self.urls:
            url_list.append(url)

    def print_o_urls(self):
        '''输出当前o_urls中的url'''
        for url in self.o_urls:
            print url

    def run(self):
        url_list = []
        while(not self.is_finish()):
            self.get_html()
            self.get_baike_title()
            self.get_emword()
            self.get_side_entity()
            self.get_urls()
            self.transformation()
            self.print_urls(url_list)
            self.switch_url()
        return url_list


    def get_all_urls(self, url_list):
        sentence_list = []


# 遍历网页xml树，获取文字
def dfs(node, l):
    if isinstance(node, bs4.element.NavigableString):
        if len(node.string) > 2:
            l.append(node.string)
        return
    for (k, v) in node.attrs.items():
        if len(v) > 2:
            l.append(v)
    for sub in node.contents:
        dfs(sub, l)

# 输入搜索关键词
def get_candidate_sen(keyword):
    c = crawler(keyword)
    c.set_timeout(10)
    c.set_total_pages(2)
    url_list = c.run()
    # print url_list
    attri_word = c.em_word
    # print c.exact_answer
    sen_list = set()
    headersParameters = {    #发送HTTP请求时的HEAD信息，用于伪装为浏览器
        'Connection': 'Keep-Alive',
        'Accept': 'text/html, application/xhtml+xml, */*',
        'Accept-Language': 'en-US,en;q=0.8,zh-Hans-CN;q=0.5,zh-Hans;q=0.3',
        'Accept-Encoding': 'gzip, deflate',
        'User-Agent': 'Mozilla/6.1 (Windows NT 6.3; WOW64; Trident/7.0; rv:11.0) like Gecko'
    }
    # 如果有KBanswer就节省时间
    # if len(c.exact_answer) > 0:
    #     return list(sen_list), c.exact_answer, c.side_entity, c.baike_title
    url_list.append('https://www.baidu.com/baidu?wd='+quote(str(c.keyword))+'&tn=monline_dg&ie=utf-8')
    for url in url_list:
        try:
            r = requests.get(url, timeout=10, headers=headersParameters)
            if r.status_code != 200:
                continue
            charset = pick_charset(r.text)
            html = r.content
            html_doc = html.decode(charset, "ignore")
            html_doc = html_doc.replace('<em>', '')
            html_doc = html_doc.replace('</em>', '')
            #解析内容
            soup = BeautifulSoup(html_doc, 'lxml')
            l = []
            dfs(soup, l)
            l = sen_filter(l, keyword.split()[0], attri_word)
            sen_list |= set(l)
        except :
            continue
        # except urllib2.HTTPError, error:
        #     contents = error.read()
        #     print contents
    # for sen in sen_list:
    #     print sen
    print keyword + "\t\t" + str(len(sen_list))
    return list(sen_list), c.exact_answer, c.side_entity, c.baike_title


# 过滤掉疑问句以及试题风格的句子,生成keyword+'\t'+sentence格式
def sen_filter(sen_list, entity, em_word):
    fin_list = []
    del_list = ['（ ）', '()', '[]', '（）', '?', '？', 'A', 'B', 'C', 'D', '（　　）', '是_', '：_', '完成下列问题','读图', '据此回', '回答下列']
    del_pre_list = ['什么', '哪些', '哪种', '哪个']
    for sen in sen_list:
        # 句中必须要有实体？？？改为删除后两个字，但长度必须大于2
        subLen = 6 if len(entity) >= 12 else 3
        if entity[0:len(entity)-subLen] not in sen:
            continue
        # 去除选择题、疑问句等
        del_flag = True
        for d in del_list:
            if d in sen:
                del_flag = False
                continue
        if del_flag == False:
            continue
        flag = True
        for em in em_word:
            if em in sen:
                sub = sen[:sen.index(em)]
                for pre in del_pre_list:
                    if sub.endswith(pre):
                        flag = False
                        continue
                if flag == True:
                    sen = re.sub("[/r/n/n/t]", '', sen)
                    fin_list.append(em + '\t' + sen.strip())
                #continue
    return fin_list


def simple_test(keyword):
    totalpages = 1
    timeout = 10
    c = crawler(keyword)
    if timeout != None:
        c.set_timeout(timeout)
    if totalpages != None:
        c.set_total_pages(totalpages)
    url_list = c.run()
    html = c.html
    #解析内容
    soup = BeautifulSoup(html, 'lxml')
    list = soup.find_all('em')
    for node in list:
        print node.string


def sen_crawler(dic_path, result_path):
    query_list = pre_work.read_file(dic_path)
    sen_list = []
    # 为了处理有的网页被遗漏，所以设置5次还连接不上才放弃。
    visit = [5 for i in range(len(query_list))]
    pre_work.erase_file(result_path)
    while find_index(visit) != -1:
        empty_index = find_index(visit)
        temp_list = []
        try:
            type = query_list[empty_index].split('\t', 1)
            t_list, exact_answer, side_entity, baike_title= get_candidate_sen(type[0])
            # for en in side_entity:
            #     print en
            temp_list.append(type[0]+':::'+str(len(t_list)+1)+':::'+type[1])
            temp_list.append('KBanswer+++'+exact_answer+'\t\t'+'Title@@@'+baike_title)
            temp_list.append('side_entity+++'+str(len(side_entity)))
            temp_list.extend(side_entity)
            temp_list.extend(t_list)
            pre_work.append_file(result_path, temp_list)
            visit[empty_index] = 0
        except:
            visit[empty_index] -= 1
            continue
    # pre_work.write_file(result_path, sen_list)
    # for query in query_list:
    #     try:
    #         temp_list = get_candidate_sen(query)
    #         sen_list.append(query+':::'+len(temp_list))
    #         sen_list.extend(temp_list)
    #     except:
    #         continue
    # pre_work.write_file(result_path, sen_list)


def find_index(visit):
    for i in range(len(visit)):
        if visit[i] > 0:
            return i
    return -1

def pick_charset(html):
    """
    从文本中提取 meta charset
    :param html:
    :return:
    """
    charset = None
    m = re.compile('<meta .*(http-equiv="?Content-Type"?.*)?charset="?([a-zA-Z0-9_-]+)"?', re.I).search(html)
    if m and m.lastindex == 2:
        charset = m.group(2).lower()
    return charset


if __name__ == '__main__':
    # sys.argv[1]E://workspace/StanfordDP/data/query/
    # query_path = "E://workspace/StanfordDP/data/query/sample.txt"
    # candidate_path = "E://workspace/StanfordDP/data/canSen/sample.txt"
    # query_path = sys.argv[1]
    # candidate_path = sys.argv[2]
    # sen_crawler(query_path, candidate_path)

    # for sen in sen_list:
    #     print sen
    #simple_test('北京 气候类型')
    path = "E:/workspace/StanfordDP/data/dic/Entity_dic"
    dirs = os.listdir(path)
    dirs.remove('dazhou.txt')
    for file in dirs:
        print file
        query_path = "E://workspace/StanfordDP/data/query/"+file
        candidate_path = "E://workspace/StanfordDP/data/canSen/"+file
        sen_crawler(query_path, candidate_path)
