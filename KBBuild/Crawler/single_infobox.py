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

reload(sys)
sys.setdefaultencoding('utf-8')


def get_infobox(url):
    headersParameters = {    #发送HTTP请求时的HEAD信息，用于伪装为浏览器
        'Connection': 'Keep-Alive',
        'Accept': 'text/html, application/xhtml+xml, */*',
        'Accept-Language': 'en-US,en;q=0.8,zh-Hans-CN;q=0.5,zh-Hans;q=0.3',
        'Accept-Encoding': 'gzip, deflate',
        'User-Agent': 'Mozilla/6.1 (Windows NT 6.3; WOW64; Trident/7.0; rv:11.0) like Gecko'
    }

    content = []
    url = "https://baike.baidu.com" + url
    try:
        session = requests.session()
        r = requests.get(url, timeout=10, headers=headersParameters)
        if r.status_code != 200:
            return None
        r.encoding = 'utf-8'
        html = r.text
        soup = BeautifulSoup(html, 'lxml')
        dl_list = soup.find_all('dl')
        # class_list = [u"basicInfo-block basicInfo-left", u"basicInfo-block basicInfo-right", u"index "]
        for dl in dl_list:
            temp = []
            if u'class' in dl.attrs and dl['class'][0] == 'basicInfo-block':
                for child in dl.contents:
                    if isinstance(child, bs4.element.Tag) and child['class'][1] == 'name':
                       temp.append(key_extractor(child))
                    elif isinstance(child, bs4.element.Tag) and child['class'][1] == 'value':
                        temp.append(value_extractor(child))
                    if len(temp) >= 2:
                        content.append(temp[0])
                        temp[:] = []
    except:
       return None
    return content


def key_extractor(tag):
    key = ''
    if tag.string is not None:
        key = tag.string.replace(' ', '')
    return key


# 抽取value含有超链接的话就实体@@@超链接
def value_extractor(tag):
    value_str = ''
    try:
        for node in tag.contents:
            if isinstance(node, bs4.element.NavigableString) and node.string != '\n' :
                value_str += node.string.strip()
            elif isinstance(node, bs4.element.Tag) and 'href' in node.attrs :
                value_str += '['+node.string+'@@@'+node['href']+']'
    except:
        return ''
    return value_str


# 词典文件、要写入Infobox信息的文件
def dic_crawler(dic_path, file_path):
    dic_path = unicode(dic_path , "utf8")
    dic = pre_work.read_file(dic_path)
    result = []
    for word in dic[:]:
        info = get_infobox(word)
        print info
        if info != '':
            result.append(info)
    pre_work.write_file(file_path, result)


def get_single_infobox(url, file_path):
    info = get_infobox(url)
    if info != None:
        pre_work.write_file(file_path, info)


if __name__ == '__main__':
    # url = '/item/028'
    # file_path = 'E:/workspace/StanfordDP/data/temp_info.txt'
    url = sys.argv[1]
    file_path = sys.argv[2]
    # dic_path = "E://workspace/StanfordDP/data/dic/Entity_dic/sheng.txt"
    # baike_path = "E://workspace/StanfordDP/data/baike_page/sheng.txt"
    get_single_infobox(url, file_path)
