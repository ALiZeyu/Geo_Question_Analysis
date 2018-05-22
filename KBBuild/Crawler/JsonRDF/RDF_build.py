#!/usr/bin/python
# ^_^ coding:utf8 ^_^
import re
import sys
import pre_work
import os
reload(sys)
sys.setdefaultencoding('utf-8')


# 中文名:::[热带草原气候@@@/item/%E7%83%AD%E5%B8%A6%E8%8D%89%E5%8E%9F%E6%B0%94%E5%80%99]去掉其中的超链接
def plain_txt_extraction(line):
    # line = '热带稀树草原气候[https://baike.baidu.com/item/%E7%83%AD%E5%B8%A6%E8%8D%89%E5%8E%9F%E6%B0%94%E5%80%99/8729269?fromtitle=%E7%83%AD%E5%B8%A6%E7%A8%80%E6%A0%91%E8%8D%89%E5%8E%9F%E6%B0%94%E5%80%99&fromid=5940816]		中文名:::[热带草原气候@@@/item/%E7%83%AD%E5%B8%A6%E8%8D%89%E5%8E%9F%E6%B0%94%E5%80%99]		外文名:::tropical savanna climate		气候特征:::全年高温，分明显的干湿两季		分布区域:::南、北纬10°至回归线之间		代表植物:::[猴面包树@@@/item/%E7%8C%B4%E9%9D%A2%E5%8C%85%E6%A0%91]、[纺锤树@@@/item/%E7%BA%BA%E9%94%A4%E6%A0%91]等。		代表动物:::象、长颈鹿、河马、犀牛、非洲狮		成因:::受[赤道低气压带@@@/item/%E8%B5%A4%E9%81%93%E4%BD%8E%E6%B0%94%E5%8E%8B%E5%B8%A6]和[信风带@@@/item/%E4%BF%A1%E9%A3%8E%E5%B8%A6]交替控制		主要分布大洲:::非洲 南美洲 大洋洲		代表城市:::[巴马科@@@/item/%E5%B7%B4%E9%A9%AC%E7%A7%91]'
    l = re.findall('\[[^\[\]]+\]', line)
    for sub_str in l:
        entity = '' if sub_str.find('@@@') <= 0 else sub_str[1:].split('@@@')[0]
        if entity == '':
            line += '\t\tBaikeURL:::'+sub_str[1:-1]
        line = line.replace(sub_str, entity)
    return line


def infobox_to_plain(path, infobox_list, file):
    content = pre_work.read_file(path)
    list = []
    for line in content:
        tline = plain_txt_extraction(line)
        list.append(file[:-4]+'@@@'+tline)
    infobox_list.extend(list)




if __name__ == '__main__':
    path = "E://workspace/StanfordDP/data/baike_page"
    dirs = os.listdir(path)
    infobox_list = []
    for file in dirs:
        infobox_to_plain(path+'/'+file, infobox_list,file)
    print len(infobox_list)
    pre_work.write_file('BaikeInfobox.txt', infobox_list)