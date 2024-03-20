def calc(a, b):
    ret = a + b
    ret = ''.join(sorted(ret))
    str = ''
    for i in range(len(ret)-1):
        if ret[i] == ret[i+1]:
            continue
        if i != 0 and ret[i] == ret[i-1]:
            continue
        str += ret[i]
    if ret[-1] != ret[-2]:
        str += ret[-1]
    return str

def update(list):
    first = list[0]
    for i in range(len(list)-1):
        list[i] = calc(list[i], list[i+1])
    list[-1] = calc(list[-1], first)
    return

def success(list):
    flag = True
    for i in list:
        if i != '':
            flag = False
            break
    return flag

def output(list):
    ret ='['
    for i in list:
        for j in i:
            ret += str(ord(j) - 31)
            ret += ' '
        ret += ', '
    return ret


def simulation(n):
    flag = False
    list = []
    # 如果直接用数字表示，只能表示9个棋子的情况，一进位字符串就表示不了了
    # 其实这是设计的时候的一个疏忽，现在暂时先拓展到acsii的部分字符上去
    if n + 32 > 127:
        print("Sorry! Too many chess pieces!")
        return
    for i in range(n):
        list.append(str(chr(32+i)))
        # list.append(str(i+1))
    for i in range(300):
        print(str(i)+output(list))
        update(list)
        if success(list):
            print(str(n) + ' chess pieces, succeed after ' + str(i) + ' round update')
            flag = True
            break
    return
        # if i == 99:
        #     # print('Cannot succeed within 100 rounds of update.')


# for i in range(3,80):
#     simulation(i)
simulation(12)