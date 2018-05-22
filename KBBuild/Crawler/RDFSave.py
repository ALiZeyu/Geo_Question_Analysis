#coding:utf8  
import rdflib
from rdflib import Namespace
import pre_work
import re

namespace = Namespace("http://nlp.nju.edu.cn/lizy/GeoKG/")

# g = rdflib.Graph()
# has_border_with = rdflib.URIRef('http://www.example.org/has_border_with')
# located_in = rdflib.URIRef('http://www.example.org/located_in')
# type = rdflib.URIRef('http://www.example.org/type')
#
# germany = rdflib.URIRef('http://www.example.org/country1')
# france = rdflib.URIRef('http://www.example.org/country2')
# china = rdflib.URIRef('http://www.example.org/country3')
# mongolia = rdflib.URIRef('http://www.example.org/country4')
#
# europa = rdflib.URIRef('http://www.example.org/part1')
# asia = rdflib.URIRef('http://www.example.org/part2')
#
# g.add((germany,has_border_with,france))
# g.add((china,has_border_with,mongolia))
# g.add((germany,located_in,europa))
# g.add((france,located_in,europa))
# g.add((china,located_in,asia))
# g.add((mongolia,located_in,asia))
# g.add((germany,type,rdflib.Literal('国家')))
#
#
# # q = "select ?country where { ?country <http://www.example.org/located_in> <http://www.example.org/part1> }"
# q = "select ?value where{ <http://www.example.org/country1> <http://www.example.org/type> ?value }"
# # x = g.query(q)
# # print list(x)
# # write graph to file, re-read it and query the newly created graph
# g.serialize("graph.rdf")
# g1 = rdflib.Graph()
# g1.parse("graph.rdf", format="xml")
# x1 = g1.query(q)
# print list(x1)
# print list(x1)[0][0]

# graph = rdflib.Graph('Sleepycat')

#first time create the store:
# graph.open('myRDFLibStore.rdf', create = True)

# graph = rdflib.Graph()
#
# s = rdflib.URIRef('牛膝')
# p = rdflib.URIRef('功效属性')
# o = rdflib.URIRef('活血')
#
# graph.add((s, p, o))
# # 以n3格式存储
# graph.serialize('zhongyao.rdf', format='n3')

# s = rdflib.URIRef('牛膝')
# p = rdflib.URIRef('http://www.example.org/功效属性')
# o = rdflib.URIRef('http://www.example.org/活血')
#
# g1 = rdflib.Graph()
# g1.add((namespace.bbb, p, o))
# g1.add((namespace.cc, rdflib.URIRef('http://www.example.org/中文名称'), rdflib.Literal('美国')))
# g1.add((namespace.cc, rdflib.URIRef('http://www.example.org/首都'), rdflib.Literal('华盛顿')))
# g1.serialize('zhongyao.rdf') # 默认以'xml'格式存储
#
# g2 = rdflib.Graph()
# g2.parse('zhongyao.rdf', format='xml') # 解析rdf文件时，需要指定格式
# # subject = g2.subjects(rdflib.URIRef('http://www.example.org/中文名称'), rdflib.Literal('美国'))
# # result = list(subject)
# # obj = g2.objects(result[0], rdflib.URIRef('http://www.example.org/首都'))
# # for o in obj:
# #     print o
# q = "select ?p where{ <http://nlp.nju.edu.cn/lizy/GeoKG/cc> ?p ?o }"
# x = g2.query(q)
# print list(x)

# 抽取所有关系
def predicate_extraction(triple):
    p_set = set()
    for line in triple:
        array = line.split('\t\t')
        for string in array:
            if string.find(':::') > 0:
                predicate = re.sub('\s+', '', string.split(':::')[0])
                predicate = predicate[:-1] if predicate.endswith('：') else predicate
                p_set.add(re.sub('\s+', '', string.split(':::')[0]))
    return list(p_set)


def GeoKGBuild(path):
    prefix = 'http://nlp.nju.edu.cn/lizy/GeoKG/'
    plain_triple = pre_work.read_file(path)
    predicate_list = predicate_extraction(plain_triple)
    g1 = rdflib.Graph()
    count = 0
    pre_work.write_file("data/alltype.txt", predicate_list)
    #把所有的关系存储起来
    for p in predicate_list:
        g1.add((rdflib.URIRef(prefix+p),rdflib.URIRef(prefix+'type'),rdflib.Literal('property')))
        count+=1
    num = 0
    print count
    for line in plain_triple:
        entity = rdflib.URIRef(prefix+'e'+str(num))
        array = line.split('\t\t')
        for po in array:
            if po.find('@@@') > 0:
                eclass = po.split('@@@')[0]
                g1.add((entity, rdflib.URIRef(prefix+'class'), rdflib.Literal(eclass)))
                count+=1
            else:
                p = re.sub('\s+', '', po.split(':::')[0])
                p = p[:-1] if p.endswith('：') else p
                o = re.sub('\s+', '', po.split(':::')[1])
                g1.add((entity,rdflib.URIRef(prefix+p), rdflib.Literal(o)))
                count+=1
    g1.serialize('GeoKG.rdf')
    print count


if __name__ == '__main__':
    GeoKGBuild('E:/PyWorkspace/Crawler/JsonRDF/BaikeInfobox.txt')