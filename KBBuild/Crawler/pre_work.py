import sys
reload(sys)
sys.setdefaultencoding('utf-8')
import codecs


def read_file(path):
    f = codecs.open(path, 'r', encoding = 'utf-8')
    s = set()
    l = []
    for word in f.readlines():
        s.add(word.strip().encode())
        # l.append(word.strip().encode())
	l = list(s)
    return l

def write_file(path, data):
    f = codecs.open(path, 'w', encoding='utf-8')
    for sen in data:
        if sen is not None:
            f.write(sen + '\n')
    f.close()

def append_file(path, data):
    f = codecs.open(path, 'a', encoding='utf-8')
    for sen in data:
        f.write(sen + '\n')
    f.close()

def erase_file(path):
    f = open(path, 'w')
    f.truncate()

