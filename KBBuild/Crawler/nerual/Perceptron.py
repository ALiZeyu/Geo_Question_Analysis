# ^_^ coding:utf8 ^_^


class Perceptron:
    def __init__(self, len, activator):
        self.activator = activator
        self.weight = [0.0 for i in range(len)]
        self.bais = 0.0

    def __str__(self):
        '''
        打印学习到的权重、偏置项
        '''
        return 'weights\t:%s\nbias\t:%f\n' % (self.weight, self.bais)

    def single_predict(self, data):
        label = self.activator(sum(map(lambda (x,w): x*w, zip(data, self.weight))) + self.bais)
        return label

    def train(self, train_data, golden_label, epoch, rate):
        for i in range(epoch):
            for (data, label) in zip(train_data, golden_label):
                delta = label - self.single_predict(data)
                if delta != 0:
                    self.bais += delta * rate
                    self.weight = map(lambda (x, w): w + rate*delta*x, zip(data, self.weight))


def f(value):
    return 1 if value>0 else 0


if __name__ == '__main__':
    p = Perceptron(2, f)
    train_data = [[0,0],[0,1],[1,0],[1,1]]
    golden_label = [0.0, 1.0, 1.0, 1.0]
    p.train(train_data, golden_label, 10, 0.1)
    print(p.__str__())
    print p.single_predict([1,1])
    print p.single_predict([1,0])