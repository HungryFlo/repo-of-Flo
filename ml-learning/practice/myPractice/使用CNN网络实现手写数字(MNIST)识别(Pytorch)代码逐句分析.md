# 使用CNN网络实现手写数字(MNIST)识别(Pytorch)代码逐句分析

## 图像导入与预处理

```python
# 图像预处理，使用Compose把多个步骤整合到一起
# ToTensor()  可以把灰度范围从0-255变换到0-1之间
# Normalize((0.5,0.5,0.5),(0.5,0.5,0.5)) 把0-1变换为(-1,1)
# Normalize就是执行：image = (image-mean)/std
transform = transforms.Compose([transforms.ToTensor(),
                                transforms.Normalize(mean=[0.5], std=[0.5])])

# 导入训练数据集和测试数据集
train_data = datasets.MNIST(root = "./data/",
                            transform = transform,
                            train = True,
                            download = True)
test_data = datasets.MNIST(root = "./data/",
                           transform = transform,
                           train = False)

# num_workers表示进程数，在windows系统中只能取0，否则会报错
train_loader = torch.utils.data.DataLoader(train_data, batch_size = 64,
                                           shuffle = True, num_workers = 0)
test_loader = torch.utils.data.DataLoader(test_data, batch_size = 64,
                                          shuffle = True, num_workers = 0, drop_last=True)
```

## 展示一个batch的数据

```python
# 显示一个batch的训练集数据
images, labels = next(iter(train_loader))
# torchvision.utils.make_grid() 用于可视化一组图像，常用于显示图像样本
img = utils.make_grid(images)
img = img.numpy().transpose(1, 2, 0)
# In the context of image data,
# this is often needed because PyTorch represents images in the format (C, H, W),
# where C is the number of channels (in this case, 1 for grayscale), H is the height, and W is the width.
# Matplotlib expects images in the format (H, W, C).
# So, the transpose swaps the second (H) and third (W) dimensions to match the required format.

# 在导入图片时将图片归一化normalize了，在显示的时候就会出现问题，所以要先算回去
std = [0.5]
mean = [0.5]
img = img * std + mean
# 循环输出label
for i in range(64):
    print(labels[i], end=" ")
    i += 1
    if i%8 == 0:
        print(end='\n')
#
plt.imshow(img)
plt.show()
```

## CNN网络

```python
# CNN 网络
# 继承nn.Module类
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
```

## 损失函数与优化函数

```python
# 损失函数和优化函数
# 损失函数为交叉熵损失
criterion = nn.CrossEntropyLoss()
# 优化函数为随机梯度下降
optimizer = optim.SGD(net.parameters(), lr=0.001, momentum=0.9)
```

## 模型训练与保存

```python
# 模型训练
# 先定义三个数组来存放结果
train_accs = []
train_loss = []
test_accs = []

# 选择训练设备
device = torch.device("cuda:0" if torch.cuda.is_available() else "cpu")
net = net.to(device)

# 选择epoch为3
for epoch in range(3):
    running_loss = 0.0
    for i, data in enumerate(train_loader, 0):
        # 取出训练集的一个batch的数据放到指定的设备中（上面指定的GPU或CPU）
        inputs, labels = data[0].to(device), data[1].to(device)
        # 将梯度初始化为0
        optimizer.zero_grad()

        # 前向 + 后向 + 优化
        # 用现有的模型对结果进行预测
        outputs = net(inputs)
        # 计算损失
        loss = criterion(outputs, labels)
        # 下面的两句是“固定搭配”
        # 将损失loss 向输入侧进行反向传播，累积到w.grad中
        loss.backward()
        # 优化器对参数进行更新，即w = w - lr * w.grad
        optimizer.step()

        # loss 的输出，每100个 batch 输出的 loss 均值
        # item()是把单元素的tensor类型转换为数字类型
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
```

## 画出训练过程

```python
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
```

## 展示一个batch的标签与预测结果

```python
dataiter = iter(test_loader)
images, labels = next(dataiter)

# 显示一个batch的测试图片
test_img = utils.make_grid(images)
test_img = test_img.numpy().transpose(1,2,0)
std = [0.5, 0.5, 0.5]
mean = [0.5, 0.5, 0.5]
test_img = test_img*std+0.5
plt.imshow(test_img)
plt.show()
print('GroundTruth: ', ' '.join('%d' % labels[j] for j in range(64)))

# 从存储中加载训练好的模型
test_net = CNN()
test_net.load_state_dict(torch.load(PATH))
# 用模型进行判断
test_out = test_net(images)

# torch.max可用于获取tensor对应位置的最大值及其下标
# 1. 返回值是一个元组，0元素为最大的那些元素，在这里就是模型算出来的概率值
#    1元素为这些元素的下标，这里就是概率值对应的各个预测结果
# 2. 参数中dim的选择很有讲究：2维数据，dim=0为行间比较（选择每列的最大值）dim=1为列间比较
#    3维数据，dim=0为通道间比较，dim=1为行间比较，dim=2为列间比较
#    总之就是，dim从小到大，比较对象从外层到内层，从宏观到微观

# 用predicted数组变量记录对于每个图像的预测值
_, predicted = torch.max(test_out, dim=1)
# 输出这个batch的预测结果
print('Predicted: ', ' '.join('%d' % predicted[j]
                              for j in range(64)))
```

## 输出测试集上的整体正确率与每个类的正确率

```python
# 输出测试集上的整体正确率
correct = 0
total = 0
# with语句所做的事情就好像是为下面的代码开辟一块环境，并在运行后自动退出
# 这里就是使模型应用过程中不在进行自动微分
with torch.no_grad():
    for data in test_loader:
        images, labels = data
        outputs = test_net(images)
        _, predicted = torch.max(outputs.data, 1)
        # 下面是加上一个labels的长度，而不是1，
        # 因为是一个batch一下子读进来的，
        # 每个batch的大小就是labels数组的长度
        total += labels.size(0)
        correct += (predicted == labels).sum().item()

print('Accuracy of the network on the test images: %d %%' %
      (100 * correct / total))

# 10个类别各自的准确率
# 初始化每个类中的正确个数和总个数
class_correct = list(0. for i in range(10))
class_total = list(0. for i in range(10))
# 仍然是不需要自动求导
with torch.no_grad():
    for data in test_loader:
        images, labels = data
        outputs = test_net(images)
        _, predicted = torch.max(outputs, 1)
        # Numpy数组运算：size相同的数组间比较可以直接生成布尔值数组
        c = (predicted == labels)

        # 一个batch是64张图片，
        # 但是这里很容易忽略“最后一个batch不全”的问题
        # 因此最好在前面的test_loader中加上 drop_last=True
        for i in range(64):
            label = labels[i]
            # 布尔类是int类的子类，可以直接进行加减运算
            class_correct[label] += c[i].item()
            class_total[label] += 1
for i in range(10):
    print('Accuracy of %d : %2d %%' %
          (i, 100 * class_correct[i] / class_total[i]))
```

## 学习材料

[使用Pytorch框架的CNN网络实现手写数字（MNIST）识别 | BraveY](https://bravey.github.io/2020-03-13-使用Pytorch框架的CNN网络实现手写数字（MNIST）识别)