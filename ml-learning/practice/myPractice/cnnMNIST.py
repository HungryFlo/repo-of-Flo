import numpy as np
import torch.cuda
from torch import nn
from PIL import Image
import matplotlib.pyplot as plt
import os
from torchvision import datasets, transforms, utils
import torch.nn.functional as F
import torch.optim as optim

# 图像预处理，使用Compose把多个步骤整合到一起
# 这一步实现了向
transform = transforms.Compose([transforms.ToTensor(),
                                transforms.Normalize(mean=[0.5], std=[0.5])])

train_data = datasets.MNIST(root = "./data/",
                            transform = transform,
                            train = True,
                            download = True)
test_data = datasets.MNIST(root = "./data/",
                           transform = transform,
                           train = False)

train_loader = torch.utils.data.DataLoader(train_data, batch_size = 64,
                                           shuffle = True, num_workers = 0)
test_loader = torch.utils.data.DataLoader(test_data, batch_size = 64,
                                          shuffle = True, num_workers = 0)

images, labels = next(iter(train_loader))
img = utils.make_grid(images)
img = img.numpy().transpose(1, 2, 0)

std = [0.5]
mean = [0.5]
img = img * std + mean # ?
for i in range(64):
    print(labels[i], end=" ")
    i += 1
    if i%8 == 0:
        print(end='\n')
plt.imshow(img)
plt.show()

# CNN 网络
class CNN(nn.Module):
    def __init__(self):
        super(CNN, self).__init__()
        self.conv1 = nn.Conv2d(1, 32, kernel_size=3, stride=1, padding=1)
        self.pool = nn.MaxPool2d(2, 2)
        self.conv2 = nn.Conv2d(32, 64, kernel_size=3, stride=1, padding=1)
        self.fc1 = nn.Linear(64 * 7 * 7, 1024)
        self.fc2 = nn.Linear(1024, 512)
        self.fc3 = nn.Linear(512, 10)

    def forward(self, x):
        x = self.pool(F.relu(self.conv1(x)))
        x = self.pool(F.relu(self.conv2(x)))

        x = x.view(-1, 64 * 7 * 7)  # 将数据平整为一维的
        x = F.relu(self.fc1(x))
        x = F.relu(self.fc2(x))
        x = self.fc3(x)
        return x


net = CNN()

# 损失函数和优化函数
criterion = nn.CrossEntropyLoss()
optimizer = optim.SGD(net.parameters(), lr=0.001, momentum=0.9)

# optimizer = torch.optim.Adam(net.parameters(), lr = 1e-2)

# 模型训练
train_accs = []
train_loss = []
test_accs = []

device = torch.device("cuda:0" if torch.cuda.is_available() else "cpu")
net = net.to(device)

for epoch in range(3):
    running_loss = 0.0
    for i, data in enumerate(train_loader, 0):
        inputs, labels = data[0].to(device), data[1].to(device)
        optimizer.zero_grad()

        # 前向 + 后向 + 优化
        outputs = net(inputs)
        loss = criterion(outputs, labels)
        loss.backward()
        optimizer.step()

        # loss 的输出，每一百个 batch 输出的 loss 均值
        running_loss += loss.item()
        if i%100 == 99:
            print('[%d,%5d] loss: %.3f'%
                  (epoch+1, i+1, running_loss/100))
            running_loss = 0.0
        train_loss.append(loss.item())

        correct = 0
        total = 0
        _, predicted = torch.max(outputs.data, 1)
        total = labels.size(0)
        correct = (predicted == labels).sum().item()
        train_accs.append(100*correct/total)

print('Finished Training')

# 模型的保存
PATH = './mnist_net.pth'
torch.save(net.state_dict(), PATH)

def draw_train_process(title, iters, costs, accs, label_cost, lable_acc):
    plt.title(title, fontsize=24)
    plt.xlabel("iter", fontsize=20)
    plt.ylabel("acc(\%)", fontsize=20)
    plt.plot(iters, costs, color='red', label=label_cost)
    plt.plot(iters, accs, color='green', label=lable_acc)
    plt.legend()
    plt.grid()
    plt.show()

train_iters = range(len(train_accs))
draw_train_process('training', train_iters, train_loss, train_accs,'trainning loss', 'training acc')

dataiter = iter(test_loader)
images, labels = dataiter.next()

test_img = utils.make_grid(images)
test_img = test_img.numpy().transpose(1,2,0)
std = [0.5, 0.5, 0.5]
mean = [0.5, 0.5, 0.5]
test_img = test_img*std+0.5
plt.imshow(test_img)
plt.show()
print('GroundTruth: ', ' '.join('%d' % labels[j] for j in range(64)))

test_net = CNN()
test_net.load_state_dict(torch.load(PATH))
test_out = test_net(images)

# torch.max可用于获取tensor对应位置的最大值及其下标
# 1. 返回值是一个元组，0元素为最大的那些元素，在这里就是模型算出来的概率值
#    1元素为这些元素的下标，这里就是概率值对应的各个预测结果
# 2. 参数中dim的选择很有讲究：2维数据，dim=0为行间比较（选择每列的最大值）dim=1为列间比较
#    3维数据，dim=0为通道间比较，dim=1为行间比较，dim=2为列间比较
#    总之就是，dim从小到大，比较对象从外层到内层，从宏观到微观

# 用predicted数组变量记录对于每个图像的预测值
_, predicted = torch.max(test_out, dim=1)
# 输出预测结果
print('Predicted: ', ' '.join('%d' % predicted[j]
                              for j in range(64)))

# 输出测试集上的整体正确率
correct = 0
total = 0
with torch.no_grad():
    for data in test_loader:
        images, labels = data
        outputs = test_net(images)
        _, predicted = torch.max(outputs.data, 1)
        total += labels.size(0)
        correct += (predicted == labels).sum().item()

print('Accuracy of the network on the test images: %d %%' %
      (100 * correct / total))

# 10个类别的准确率
class_correct = list(0. for i in range(10))
class_total = list(0. for i in range(10))
with torch.no_grad():
    for data in test_loader:
        images, labels = data
        outputs = test_net(images)
        _, predicted = torch.max(outputs, i)
        c = (predicted == labels)
        for i in range(10):
            label = labels[i]
            class_correct[label] += c[i].item()
            class_total[label] += 1
for i in range(10):
    print('Accuracy of %d : %2d %%' %
          (i, 100 * class_correct[i] / class_total[i]))