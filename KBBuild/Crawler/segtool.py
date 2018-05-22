#!/usr/bin/python
# ^_^ coding:utf8 ^_^
import sys
reload(sys)
sys.setdefaultencoding('utf-8')
import codecs



def read_file(path):
    f = codecs.open(path, 'r', encoding = 'utf-8')
    l = []
    for word in f.readlines():
        # s.add(word.strip().encode())
        l.append(word.strip().encode())
    return l

def write_file(path, data):
    f = codecs.open(path, 'w', encoding='utf-8')
    for sen in data:
        f.write(sen + '\n')
    f.close()


# import thulac
# thu1 = thulac.thulac(seg_only=False, user_dict='F:/final_test/data/all_dic.txt')  #只进行分词，不进行词性标注
# thu1.cut_f("F:/final_test/data/seg/test.txt", "data/output.txt")
import jieba
jieba.load_userdict('F:/final_test/data/all_dic.txt')
test_list = read_file('F:/final_test/data/seg/test.txt')
result = []
for sen in test_list:
    seg_list = jieba.cut(sen)  # 默认是精确模式
    result.append(' '.join(seg_list))
write_file('data/output.txt', result)