# -*- coding: utf-8 -*-
import json
import re
import rdflib
f = open('data.json', 'r')
g1 = rdflib.Graph()
cnt = 0
for line in f:
    #txt = eval("{" + line + "}")
    txt = eval(line)
    cnt = cnt + 1
    print cnt
    try:
        s = rdflib.URIRef('http://baike.com/resource/' + txt['infoName'])
        p = rdflib.URIRef('http://baike.com/resource/开放分类')
        opentype = re.split(r',', txt['openType'])
        for i in range(len(opentype) - 1):
            o = rdflib.URIRef('http://baike.com/resource/' + opentype[i])
            g1.add((s, p, o))
        o = rdflib.URIRef('http://baike.com/resource/' +
                          opentype[len(opentype) - 1])
        g1.add((s, p, o))

        # g1.serialize('da.rdf')
        if len(txt['infoBox']) != 0:
            box = re.split(r',', txt['infoBox'])
            for num in range(len(box) - 1):
                lastbox = re.split(r':', box[num])
                print len(lastbox)
                if len(lastbox) == 2:
                    print lastbox[0]
                    print lastbox[1]
                    try:
                        rtxt = re.compile(
                            r'"| |　|    |:|：|、|。|\(|\)|（|）|℃|}|{')
                        lastKey = rtxt.sub('', lastbox[0])
                        p = rdflib.URIRef(
                            'http://baike.com/resource/' + lastKey)
                        rtxt = re.compile(r'"| |\&gt|}|{')
                        lastTest = rtxt.sub('', lastbox[1])
                        lastTestChild = re.split(
                            r'、|，|；|;|,|\|', lastTest)
                        for i in range(len(lastTestChild) - 1):
                            o = rdflib.URIRef(
                                'http://baike.com/resource/' + lastTestChild[i])
                            g1.add((s, p, o))
                        o = rdflib.URIRef(
                            'http://baike.com/resource/' + lastTestChild[len(lastTestChild) - 1])
                        g1.add((s, p, o))

                        # g1.serialize('da.rdf')
                    except:
                        pass

    except Exception as e:
        pass
g1.serialize('data.rdf')
# print box[num]

f.close()
