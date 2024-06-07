# Python程序设计综合训练实验报告

[TOC]



本项目的源代码：https://github.com/HungryFlo/Recipe-Evaluator.git

## 实验内容

设计并完成一个完整的膳食助手应用程序，实现以下功能：

1. 生成食堂供应食物的营养物质信息表

2. 通过点选食物或表格导入生成食谱

3. 计算食谱的营养指标

4. 提供均衡膳食指标推荐值指南

根据前后端分离的原则，可将上面的功能分解为以下的模块：

**后端**：

1. 通过表格处理，生成食堂供应食物的营养物质信息表
2. 计算给定食谱的各项指标的值

**前端**：

1. 页面设计
2. 信号与槽函数的构建及与后端的连接

## 实验过程及结果

### 生成食堂供应食物的营养物质信息表

#### 食堂供应食物表

食堂供应食物表中记录了“食物名称”、“主要成分”、“食物编码”、“可食部”、“价格”、“是否可半份”的信息。三餐的供应信息分别存储在三张格式相同的表格中以做区分。

下面是表格的示例：

<img src="F:\repo-of-Flo\school curriculums\PythonExperiments\Python程序设计综合训练实验报告.assets\image-20240606155938695.png" alt="image-20240606155938695" style="zoom:50%;" />

这个表格可以通过调查统计来获得。

#### 食物营养成分信息表

这个信息可以通过爬虫来获得，但是由于网站的限制，需持有营养师资格证才可以开放显示数据，因此通过搜索，最终选择购买官方的《中国食物成分表》进行参考，并补充了各食物氨基酸含量的信息，数据较为可信。

![image-20240606160435046](F:\repo-of-Flo\school curriculums\PythonExperiments\Python程序设计综合训练实验报告.assets\image-20240606160435046.png)

#### 整理整合表格

使用Python的pandas和numpy来对表格进行处理。

```python
import numpy as np
import pandas as pd
import warnings
warnings.filterwarnings('ignore')
```

设置输入和输出路径，并导入表格：

```python
# ============================读入文件==============================
canteen_foods_file = ["excel/canteen_breakfast.xlsx",
                      "excel/canteen_lunch.xlsx",
                      "excel/canteen_dinner.xlsx"]
output_path = ["output/canteen_breakfast_info.xlsx",
               "output/canteen_lunch_info.xlsx",
               "output/canteen_dinner_info.xlsx"]

id = 2

canteen_foods_df = pd.read_excel(canteen_foods_file[id])
aa_df = pd.read_excel("excel/aa_data.xlsx")
nutrition_df = pd.read_excel("excel/中国食物成分表.xls")
# print(nutrition_df.head())
# =================================================================
```

首先对表格中的异常值进行清洗：

```python
def clean_and_convert(code):
    cleaned_code = ''.join(filter(str.isdigit, str(code)))  # 移除字母，只保留数字
    if cleaned_code:  # 确保处理后的字符串不为空
        return int(cleaned_code)  # 将干净的字符串转换为整数
    else:
        return np.nan  # 如果转换后为空字符串，则返回NaN表示无法转换
# 应用该函数到'食物编码'列
canteen_foods_df['食物编码'] = canteen_foods_df['食物编码'].apply(clean_and_convert)
canteen_foods_df.loc[canteen_foods_df['主要成分'] == '土豆', '食物编码'] = 21101
```

然后将两个表格以“食物编码”为关键字进行左连接：

```python
merge_df = pd.merge(canteen_foods_df, nutrition_df, on="食物编码", how='left')
merge_df = pd.merge(merge_df,aa_df, on="主要成分", how='left')
```

对连接后产生的列名进行整理：

```python
# 使用字典映射和列表推导式来创建新的列名列表
new_columns = {old: clean_column_name(old) for old in canteen_foods_df.columns}
# 用新列名替换原DataFrame的列名
canteen_foods_df.rename(columns=new_columns, inplace=True)
# 删除无用的列
canteen_foods_df.drop(['食物名称_y', '备注,标明食物或数据的来源', '可食部分%'], axis=1, inplace=True)
```

处理原表格中存在的单元格合并情况：

```python
# 对多种成分的食物进行处理
# 指定需要进行前向填充的列
columns_to_fill = ['序号', '食物名称', '价格（元/份）','是否可半份']
# 仅对指定列进行前向填充
canteen_foods_df[columns_to_fill] = canteen_foods_df[columns_to_fill].fillna(method='ffill')

# 将无数据的值赋值为0
columns_not_to_fill_zero = ['序号', '食物名称', '主要成分', '食物编码', '可食部（克/份）', '价格（元/份）', '是否可半份']
columns_to_fill_zero = list(set(canteen_foods_df.columns).difference(set(columns_not_to_fill_zero)))
canteen_foods_df[columns_to_fill_zero] = canteen_foods_df[columns_to_fill_zero].fillna(0)
```

下面对食物进行分类，按照其主要成分的类型将其分到不同的类别中：

```python
# ====================添加每种食物的种类信息=====================
def classifier(code):
    if (code >= 11101 and code <= 19009) or (code >= 21101 and code <= 22203):
        return 1
    elif(code >= 41101 and code <= 48080) or (code >= 51001 and code <= 52008) or (code >= 61101 and code <= 66205):
        return 2
    elif (code >= 81101 and code <= 89005) or (code >= 91101 and code <= 99002) or (code >= 121101 and code <= 129013) or (code >= 111101 and code <= 114201):
        return 3
    elif (code >= 31101 and code <= 39902) or (code >= 101101 and code <= 109003) or (code >= 71001 and code <= 72019):
        return 4
    elif (code >= 191001 and code <= 192019):
        return 5
    else:
        return -1

canteen_foods_df['食物种类'] = canteen_foods_df['食物编码'].apply(classifier)
# ================================================================
```

由于营养物质表格是按照食物的主要成分分隔的，且其中所给的营养成分含量信息均为每 100g 可食部分中营养素的含量，氨基酸的值表示每 1g 蛋白质所含的氨基酸的量，但是在我们点餐时均是按份来点餐，因此我们有必要计算一下每一个最小售卖单元的食物中各种营养物质的含量。另外在计算时，还要注意到“是否可半份”这一信息，对营养物质和价格进行考虑。具体的代码如下：

```python
# =============计算各食品最小售卖单元的营养物质信息======================
# 对可半份的食物进行调整
def adjust_quantity(row):
    if row['是否可半份'] == '是':
        row['可食部（克/份）'] /= 2
        row['价格（元/份）'] /=2
    return row
# 应用自定义函数到每一行
canteen_foods_df = canteen_foods_df.apply(adjust_quantity, axis=1)

canteen_foods_df = canteen_foods_df.rename(columns={'是否可半份': '是否为半份', '可食部（克/份）': '可食部（克/单元）', '价格（元/份）': '价格（元/单元）'})

# CHECKPOINT:检查是否将半份进行处理
canteen_foods_df.to_excel("CHECKPOINT_classify_half.xlsx", index=False)

# 下面计算每种成分在最小售卖单元下的各营养含量
# 首先对氨基酸进行调整
amino_acids = ['异亮氨酸（毫克/克蛋白质）', '亮氨酸（毫克/克蛋白质）', '赖氨酸（毫克/克蛋白质）',
        '含硫氨基酸（毫克/克蛋白质）', '芳香族氨基酸（毫克/克蛋白质）', '苏氨酸（毫克/克蛋白质）',
        '色氨酸（毫克/克蛋白质）', '缬氨酸（毫克/克蛋白质）']
for aa in amino_acids:
    canteen_foods_df[aa] = canteen_foods_df[aa] * canteen_foods_df['蛋白质（克）'] * canteen_foods_df['可食部（克/单元）'] / 100
    canteen_foods_df.rename(columns={aa: aa.replace('（毫克/克蛋白质）', '（毫克）/单元')}, inplace=True)

# 定义需要调整的营养物质列表
nutrients_to_adjust = ['水分（克）', '能量（千卡）', '能量（千焦）', '蛋白质（克）', '脂肪（克）',
        '碳水化物（克）', '膳食纤维（克）', '胆固醇（毫克）', '灰分（克）', '维生素A（微克）', '胡萝卜素（微克）',
        '视黄醇（微克）', '硫胺素（毫克）', '核黄素（毫克）', '尼克酸（毫克）', '维生素C（毫克）', '维生素E（毫克）',
        'a_维生素E（毫克）', '钙（毫克）', '磷（毫克）', '钾（毫克）', '钠（毫克）', '镁（毫克）', '铁（毫克）',
        '锌（毫克）', '硒（微克）', '铜（毫克）', '锰（毫克）']
# 循环遍历营养物质列表，为每种营养物质创建每售卖单元的列
for nutrient in nutrients_to_adjust:
    adjusted_column_name = f'{nutrient}/单元'  # 根据原列名生成调整后的新列名
    canteen_foods_df[adjusted_column_name] = canteen_foods_df[nutrient] * canteen_foods_df['可食部（克/单元）'] / 100

# 定义三种不同的合并逻辑
def combine_strings(series):
    # 将字符串列以逗号连接
    return ','.join(series.dropna().astype(str))

def sum_columns(series):
    # 直接对数值列求和
    return series.sum()

def keep_first(series):
    # 仅保留第一排的值
    return series.iloc[0]

# 假设的列名列表，其中'主要成分'和'食物种类'需要特殊处理，其余为数值求和
name_col = ['食物名称']
string_cols = ['主要成分', '食物种类']
keep_cols = ['价格（元/单元）', '是否为半份']
numeric_cols = ['水分（克）/单元', '能量（千卡）/单元', '能量（千焦）/单元', '蛋白质（克）/单元',
       '脂肪（克）/单元', '碳水化物（克）/单元', '膳食纤维（克）/单元', '胆固醇（毫克）/单元', '灰分（克）/单元',
       '维生素A（微克）/单元', '胡萝卜素（微克）/单元', '视黄醇（微克）/单元', '硫胺素（毫克）/单元', '核黄素（毫克）/单元',
       '尼克酸（毫克）/单元', '维生素C（毫克）/单元', '维生素E（毫克）/单元', 'a_维生素E（毫克）/单元', '钙（毫克）/单元',
       '磷（毫克）/单元', '钾（毫克）/单元', '钠（毫克）/单元', '镁（毫克）/单元', '铁（毫克）/单元', '锌（毫克）/单元',
       '硒（微克）/单元', '铜（毫克）/单元', '锰（毫克）/单元', '异亮氨酸（毫克）/单元', '亮氨酸（毫克）/单元', '赖氨酸（毫克）/单元',
       '含硫氨基酸（毫克）/单元', '芳香族氨基酸（毫克）/单元', '苏氨酸（毫克）/单元', '色氨酸（毫克）/单元', '缬氨酸（毫克）/单元']
        # 列表中应包含所有需要求和的数值列


# 构建聚合规则字典
agg_dict = {col: keep_first for col in name_col}
agg_dict.update({col: combine_strings for col in string_cols})
agg_dict.update({col: keep_first for col in keep_cols})
agg_dict.update({col: sum_columns for col in numeric_cols})

# 使用groupby进行分组并应用聚合函数
canteen_foods_df = canteen_foods_df.groupby('序号').agg(agg_dict).reset_index()
```

最终将表格进行输出：

```python
canteen_foods_df.to_excel(output_path[id], index=False)
```

表格样例如下：

![image-20240606161244339](F:\repo-of-Flo\school curriculums\PythonExperiments\Python程序设计综合训练实验报告.assets\image-20240606161244339.png)

### 计算给定食谱的各项指标的值

```python
import pandas as pd
import numpy as np
import warnings
warnings.filterwarnings('ignore')

class Evaluator:
    def __init__(self, breakfast_refer_path="output/canteen_breakfast_info.xlsx",
                 lunch_refer_path="output/canteen_lunch_info.xlsx",
                 dinner_refer_path="output/canteen_dinner_info.xlsx",
                 gender="boy"):
        # print("记得改性别哦")
        self.breakfast_ref = pd.read_excel(breakfast_refer_path)
        self.lunch_ref = pd.read_excel(lunch_refer_path)
        self.dinner_ref = pd.read_excel(dinner_refer_path)
        self.gender = gender
```

#### 对食谱进行清理

为了增加程序的鲁棒性，需要在计算各项指标之前将食谱中的误输入项进行清理。

```python
    def clean_recipe_dict(self, recipe_dict):
        '''
        清理食物选择字典，移除无法在参考表格中找到的食物项，并返回移除的食物列表。
        '''
        removed_foods = []  # 用于记录被移除的食物名称

        # 创建一个副本以避免修改原字典
        cleaned_dict = recipe_dict.copy()

        for food, info in list(cleaned_dict.items()):  # 使用list以便在迭代过程中修改字典
            meal_time, _ = info
            food_ref = {
                '早餐': self.breakfast_ref,
                '午餐': self.lunch_ref,
                '晚餐': self.dinner_ref
            }[meal_time]

            # 尝试查找食物种类，如果不存在则移除该条目并记录
            if food_ref[food_ref["食物名称"] == food]["食物名称"].empty:
                removed_foods.append(food)
                del cleaned_dict[food]
        # if len(removed_foods) > 0:
            # print(f"被移除的食物项: {removed_foods}")

        return cleaned_dict
```

#### 计算食谱的食物多样性

根据表格中的信息计算食谱中的食物是否覆盖了推荐摄入的五大类型：

```python
    def type(self, recipe_dict):
        '''
        输入食物选择字典，返回该食谱的食物大类数
        字典结构：
        {"食物名称":("餐次", 数量), ... }
        '''
        recipe_dict = self.clean_recipe_dict(recipe_dict)
        types_set = set()
        for food, info in recipe_dict.items():
            type = set()
            if info[0] == '早餐':
                type = self.breakfast_ref.loc[self.breakfast_ref["食物名称"] == food, '食物种类']
            elif info[0] == '午餐':
                type = self.lunch_ref.loc[self.lunch_ref["食物名称"] == food, '食物种类']
            else:
                type = self.dinner_ref.loc[self.dinner_ref["食物名称"] == food, '食物种类']
            type = set(type.item())
            types_set = types_set | type
        types_set.discard(',')
        return len(types_set)
```

#### 计算食物种类数

以食物的主要成分为标准来计算食谱中食物的种类数：

```python
    def variety(self, recipe_dict):
        '''
            输入食物选择字典，返回该食谱的食物主要成分种类数
        '''
        return len(self.variety_details(recipe_dict))

    def variety_details(self,recipe_dict):
        recipe_dict = self.clean_recipe_dict(recipe_dict)
        ingredients_set = set()
        for food, info in recipe_dict.items():
            if info[0] == '早餐':
                ingredient = self.breakfast_ref.loc[self.breakfast_ref["食物名称"] == food, '主要成分']
            elif info[0] == '午餐':
                ingredient = self.lunch_ref.loc[self.lunch_ref["食物名称"] == food, '主要成分']
            else:
                ingredient = self.dinner_ref.loc[self.dinner_ref["食物名称"] == food, '主要成分']
            ingredient = set(ingredient.item().split(','))
            ingredients_set = ingredients_set | ingredient
        return ingredients_set
```

#### 计算食谱中各营养素的总量

将相应的列的值进行累加：

```python
    def calculate_nutrition(self, recipe_dict):
        """
        计算指定食物和食用份数的营养成分
        返回一个新的DataFrame，包含按食用份数调整后的营养成分以及'食物名称'和'餐次'列
        """
        recipe_dict = self.clean_recipe_dict(recipe_dict)
        nutrition_cols = ['水分（克）/单元', '能量（千卡）/单元', '能量（千焦）/单元', '蛋白质（克）/单元',
                          '脂肪（克）/单元', '碳水化物（克）/单元', '膳食纤维（克）/单元', '胆固醇（毫克）/单元',
                          '灰分（克）/单元',
                          '维生素A（微克）/单元', '胡萝卜素（微克）/单元', '视黄醇（微克）/单元', '硫胺素（毫克）/单元',
                          '核黄素（毫克）/单元',
                          '尼克酸（毫克）/单元', '维生素C（毫克）/单元', '维生素E（毫克）/单元', 'a_维生素E（毫克）/单元',
                          '钙（毫克）/单元',
                          '磷（毫克）/单元', '钾（毫克）/单元', '钠（毫克）/单元', '镁（毫克）/单元', '铁（毫克）/单元',
                          '锌（毫克）/单元',
                          '硒（微克）/单元', '铜（毫克）/单元', '锰（毫克）/单元', '异亮氨酸（毫克）/单元',
                          '亮氨酸（毫克）/单元', '赖氨酸（毫克）/单元', '含硫氨基酸（毫克）/单元', '芳香族氨基酸（毫克）/单元',
                          '苏氨酸（毫克）/单元', '色氨酸（毫克）/单元', '缬氨酸（毫克）/单元']

        # 初始化结果DataFrame时就包含'食物名称'和'餐次'列
        result_df = pd.DataFrame(columns=['食物名称', '餐次'] + nutrition_cols)

        for food, info in recipe_dict.items():
            meal_time, servings = info  # 解构info以获取餐次和份数
            if meal_time == '早餐':
                nutrition_df = self.breakfast_ref
            elif meal_time == '午餐':
                nutrition_df = self.lunch_ref
            else:
                nutrition_df = self.dinner_ref

            num = servings * 2 if nutrition_df.loc[
                                      nutrition_df['食物名称'] == food, "是否为半份"].item() == "是" else servings
            selected_nutrition = nutrition_df.loc[nutrition_df['食物名称'] == food, nutrition_cols].iloc[
                                     0] * num  # 取第一行数据并乘以份数

            # 添加'食物名称'和'餐次'到selected_nutrition
            selected_nutrition = pd.DataFrame(selected_nutrition).T  # 转置以适应append操作
            selected_nutrition['食物名称'] = food
            selected_nutrition['餐次'] = meal_time

            result_df = pd.concat([result_df,selected_nutrition], ignore_index=True)  # 使用append合并数据

        # 重新排序列顺序，确保'食物名称'和'餐次'在前
        column_order = ['食物名称', '餐次'] + [col for col in result_df.columns if col not in ['食物名称', '餐次']]
        result_df = result_df[column_order]

        # 计算总和行，但需注意排除'食物名称'和'餐次'
        numeric_cols = [col for col in result_df.columns if col not in ['食物名称', '餐次']]
        result_df.loc["sum"] = result_df[numeric_cols].apply(lambda x: sum(x), axis=0)

        result_df.columns = ['食物名称', '餐次', '水分（克）', '能量（千卡）', '能量（千焦）', '蛋白质（克）', '脂肪（克）',
                             '碳水化物（克）', '膳食纤维（克）', '胆固醇（毫克）', '灰分（克）', '维生素A（微克）',
                             '胡萝卜素（微克）',
                             '视黄醇（微克）', '硫胺素（毫克）', '核黄素（毫克）', '尼克酸（毫克）', '维生素C（毫克）',
                             '维生素E（毫克）',
                             'a_维生素E（毫克）', '钙（毫克）', '磷（毫克）', '钾（毫克）', '钠（毫克）', '镁（毫克）', '铁（毫克）',
                             '锌（毫克）', '硒（微克）', '铜（毫克）', '锰（毫克）', '异亮氨酸（毫克）',
                          '亮氨酸（毫克）', '赖氨酸（毫克）', '含硫氨基酸（毫克）', '芳香族氨基酸（毫克）',
                          '苏氨酸（毫克）', '色氨酸（毫克）', '缬氨酸（毫克）']

        return result_df
```

#### 计算食谱中一天摄入的能量总量

```python
    def energy(self,recipe_dict):
        result_df = self.calculate_nutrition(recipe_dict)
        intake = result_df.loc["sum"]['能量（千卡）'].item()
        return float(intake)
```

#### 计算能量摄入餐次比

```python
    def energy_every_meal(self,recipe_dict):
        result_df = self.calculate_nutrition(recipe_dict)
        energy_sums = result_df.groupby('餐次')['能量（千卡）'].sum().to_dict()
        return energy_sums

    def energy_proportion(self,recipe_dict):
        dict = self.energy_every_meal(recipe_dict)
        sum = 0
        for v in dict.values():
            sum += v
        for k, v in dict.items():
            dict.update({k: v/sum})
        return dict

    def energy_proportion_list(self,recipe_dict):
        dict = self.energy_proportion(recipe_dict)
        return dict["早餐"]*100, dict["午餐"]*100, dict["晚餐"]*100
```

#### 计算宏量营养素供能比

```python
    def m_energy_ratio(self,recipe_dict):
        result_df = self.calculate_nutrition(recipe_dict)
        energy = result_df.loc["sum"]['能量（千卡）'].item()
        protein = result_df.loc["sum"]['蛋白质（克）'].item() * 4
        fat = result_df.loc["sum"]['脂肪（克）'].item() * 9
        carbon = result_df.loc["sum"]['碳水化物（克）'].item() * 4
        fiber = result_df.loc["sum"]['膳食纤维（克）'].item() * 2
        result_dict = {
            "protein": protein / energy,
            "fat": fat / energy,
            "carbon": carbon / energy
        }
        # print(result_dict)
        return result_dict

    def m_energy_ratio_list(self,recipe_dict):
        dict = self.m_energy_ratio(recipe_dict)
        return dict["carbon"]*100, dict["fat"]*100, dict["protein"]*100

```

#### 计算非产能营养素与标准的差值

此处会涉及到男性和女性的推荐标准不同的问题，可以通过类成员变量来记录性别，并在初始化时指定性别：

```python
    def non_energy_nutrients(self, recipe_dict):
        '''
        :param recipe_dict:食谱字典
        :return:返回非产能营养素与标准的差值
        '''
        recipe_dict = self.clean_recipe_dict(recipe_dict)
        boy_target = {
            '钙（毫克）': 800,
            '铁（毫克）': 12,
            '锌（毫克）': 12.5,
            '维生素A（微克）': 800,
            '硫胺素（毫克）': 1.4,
            '核黄素（毫克）': 1.4,
            '维生素C（毫克）': 100
        }
        girl_target = {
            '钙（毫克）': 800,
            '铁（毫克）': 20,
            '锌（毫克）': 7.5,
            '维生素A（微克）': 700,
            '硫胺素（毫克）': 1.2,
            '核黄素（毫克）': 1.2,
            '维生素C（毫克）': 100
        }

        target = boy_target
        if self.gender == "girl":
            target = girl_target

        result_df = self.calculate_nutrition(recipe_dict)
        sum_row = result_df.iloc[-1]

        target_columns = {
            '钙（毫克）': '钙（毫克）',
            '铁（毫克）': '铁（毫克）',
            '锌（毫克）': '锌（毫克）',
            '维生素A（微克）': '维生素A（微克）',
            '硫胺素（毫克）': '硫胺素（毫克）',
            '核黄素（毫克）': '核黄素（毫克）',
            '维生素C（毫克）': '维生素C（毫克）'
        }
        data = {key: sum_row[value] for key, value in target_columns.items()}
        difference_dict = {}
        total_difference = 0
        for key in data.keys() & target.keys():
            difference_dict.update({key: abs(data[key] - target[key])})
            total_difference += abs(data[key] - target[key])
        # print(difference_dict)
        return total_difference / 1000
```

#### 计算食谱价格

计算价格时需要考虑到半份的问题：

```python
    def price(self, recipe_dict):
        recipe_dict = self.clean_recipe_dict(recipe_dict)
        price = 0
        for food, info in recipe_dict.items():
            meal_time, amount = info  # 解构info以获取餐次和份数
            if meal_time == '早餐':
                nutrition_df = self.breakfast_ref
            elif meal_time == '午餐':
                nutrition_df = self.lunch_ref
            else:
                nutrition_df = self.dinner_ref
            num = amount * 2 if nutrition_df.loc[
                                      nutrition_df['食物名称'] == food, "是否为半份"].item() == "是" else amount
            price += nutrition_df.loc[nutrition_df['食物名称'] == food, '价格（元/单元）'].item() * num
        return price
```

#### 计算蛋白质氨基酸评分

计算方法如下：
$$
\text{AAS}=\frac{\text{每克待测蛋白质中必需氨基酸的含量}}{\text{每克参考蛋白质中必需氨基酸的含量}} \cdot 100
$$

```python
    def aas(self, recipe_dict):
        recipe_dict = self.clean_recipe_dict(recipe_dict)
        # 设定标准值
        target = {
            '异亮氨酸（毫克）': 40,
            '亮氨酸（毫克）': 70,
            '赖氨酸（毫克）': 55,
            '含硫氨基酸（毫克）': 35,
            '芳香族氨基酸（毫克）': 60,
            '苏氨酸（毫克）': 40,
            '色氨酸（毫克）': 10,
            '缬氨酸（毫克）': 50
        }
        selected_columns = [
            '蛋白质（克）',
            '异亮氨酸（毫克）',
            '亮氨酸（毫克）',
            '赖氨酸（毫克）',
            '含硫氨基酸（毫克）',
            '芳香族氨基酸（毫克）',
            '苏氨酸（毫克）',
            '色氨酸（毫克）',
            '缬氨酸（毫克）'
        ]
        result_df = self.calculate_nutrition(recipe_dict)
        sum_df = result_df.groupby('餐次')[selected_columns].sum().reset_index()
        # print(sum_df)

        meals_dicts = []
        # 分组并构造餐次字典，同时获取每个餐次的蛋白质质量
        for meal_name, group_df in sum_df.groupby('餐次'):
            # 获取该餐次的蛋白质质量（克）
            protein_mass_grams = group_df["蛋白质（克）"].iloc[0]
            meal_dict = {meal_name: group_df.drop(columns=['餐次', '蛋白质（克）']).to_dict(orient='records')[0]}
            meals_dicts.append(meal_dict)

        # 初始化评估字典
        assessment = {}

        # 处理每个餐次的数据
        for meal_dict in meals_dicts:
            for meal_name, details in meal_dict.items():
                ratio_dict = {}
                # 计算每种氨基酸在该餐次中的含量（毫克/克蛋白质）
                for key, value in details.items():
                    if "（毫克）" in key:
                        ratio_dict[key] = value / protein_mass_grams

                meal_aas = float('inf')
                # 确保target是一个已定义的字典，包含氨基酸的目标含量（毫克/克蛋白质）
                for target_key in target:
                    if target_key in ratio_dict:
                        curr_aas = ratio_dict[target_key] / target[target_key] * 100
                        meal_aas = min(meal_aas, curr_aas)

                assessment[meal_name] = meal_aas
        return min(assessment.values())
```

#### 计算膳食评价得分

此处借鉴了灰色关联度分析的思想，设计了一种膳食评分计算方法。

```python
    def grey_score(self,recipe_dict):
        # type_score = self.type(recipe_dict) / 5
        # variety_score = (self.variety(recipe_dict) - 3) / (20 - 3)
        # energy_score = (self.energy(recipe_dict) - )
        type_diff = self.type(recipe_dict) - 5
        variety_diff = self.variety(recipe_dict) - 12
        energy_diff = abs((self.energy(recipe_dict) - 2400) if self.gender=="boy" else (self.energy(recipe_dict) - 1900))
        energy_proportion_diff_1 = 0 if (self.energy_proportion(recipe_dict)["早餐"] >= 0.25 and
        self.energy_proportion(recipe_dict)["早餐"] <= 0.35) else min(abs(self.energy_proportion(recipe_dict)["早餐"] - 0.25), abs(self.energy_proportion(recipe_dict)["早餐"] - 0.35))
        energy_proportion_diff_2 = 0 if (self.energy_proportion(recipe_dict)["午餐"] >= 0.3 and
                                         self.energy_proportion(recipe_dict)["午餐"] <= 0.4) else min(
            abs(self.energy_proportion(recipe_dict)["午餐"] - 0.3),
            abs(self.energy_proportion(recipe_dict)["午餐"] - 0.4))
        energy_proportion_diff_3 = 0 if (self.energy_proportion(recipe_dict)["晚餐"] >= 0.3 and
                                         self.energy_proportion(recipe_dict)["晚餐"] <= 0.4) else min(
            abs(self.energy_proportion(recipe_dict)["晚餐"] - 0.3),
            abs(self.energy_proportion(recipe_dict)["晚餐"] - 0.4))
        energy_proportion_diff = energy_proportion_diff_1 + energy_proportion_diff_2 + energy_proportion_diff_3
        m_energy_ratio_diff_1 = 0 if (self.m_energy_ratio(recipe_dict)["protein"] >= 0.1 and
                                         self.m_energy_ratio(recipe_dict)["protein"] <= 0.15) else min(
            abs(self.m_energy_ratio(recipe_dict)["protein"] - 0.1),
            abs(self.m_energy_ratio(recipe_dict)["protein"] - 0.15))
        m_energy_ratio_diff_2 = 0 if (self.m_energy_ratio(recipe_dict)["fat"] >= 0.2 and
                                      self.m_energy_ratio(recipe_dict)["fat"] <= 0.3) else min(
            abs(self.m_energy_ratio(recipe_dict)["fat"] - 0.2),
            abs(self.m_energy_ratio(recipe_dict)["fat"] - 0.3))
        m_energy_ratio_diff_3 = 0 if (self.m_energy_ratio(recipe_dict)["carbon"] >= 0.5 and
                                      self.m_energy_ratio(recipe_dict)["carbon"] <= 0.65) else min(
            abs(self.m_energy_ratio(recipe_dict)["carbon"] - 0.5),
            abs(self.m_energy_ratio(recipe_dict)["carbon"] - 0.65))
        m_energy_ratio_diff = m_energy_ratio_diff_1+m_energy_ratio_diff_2+m_energy_ratio_diff_3
        non_energy_nutrients_diff = self.non_energy_nutrients(recipe_dict)
        diff_list = [type_diff, variety_diff/10, energy_diff/1000, energy_proportion_diff, m_energy_ratio_diff, non_energy_nutrients_diff]
        max_diff = max(diff_list)
        min_diff = min(diff_list)
        score_list = []
        rho = 0.7
        for i in diff_list:
            score_list.append((rho * max_diff + min_diff)/(i + rho * max_diff))
        print(diff_list)
        score = np.mean(score_list)
        return score
```

### 页面设计

#### UI设计

通过需求分析，需要设计的页面有：

1. 首页
2. 新建食谱页
3. 膳食知识页
4. 导入文件页
5. 食谱选择页（3个）
6. 性别选择页
7. 结果展示页

设计图如下：

<img src="F:\repo-of-Flo\school curriculums\PythonExperiments\Python程序设计综合训练实验报告.assets\image-20240606165041722.png" alt="image-20240606165041722" style="zoom:50%;" />

<img src="F:\repo-of-Flo\school curriculums\PythonExperiments\Python程序设计综合训练实验报告.assets\image-20240606165101872.png" alt="image-20240606165101872" style="zoom:50%;" />

<img src="F:\repo-of-Flo\school curriculums\PythonExperiments\Python程序设计综合训练实验报告.assets\image-20240606165118109.png" alt="image-20240606165118109" style="zoom:50%;" />

<img src="F:\repo-of-Flo\school curriculums\PythonExperiments\Python程序设计综合训练实验报告.assets\image-20240606165146506.png" alt="image-20240606165146506" style="zoom:50%;" />

<img src="F:\repo-of-Flo\school curriculums\PythonExperiments\Python程序设计综合训练实验报告.assets\image-20240606165200123.png" alt="image-20240606165200123" style="zoom:50%;" />

<img src="F:\repo-of-Flo\school curriculums\PythonExperiments\Python程序设计综合训练实验报告.assets\image-20240606165221787.png" alt="image-20240606165221787" style="zoom:50%;" />

<img src="F:\repo-of-Flo\school curriculums\PythonExperiments\Python程序设计综合训练实验报告.assets\image-20240606165235329.png" alt="image-20240606165235329" style="zoom:50%;" />

#### UI实现

##### choose_breakfast

```python
from PyQt5 import QtCore, QtGui, QtWidgets


class Ui_choose_breakfast(object):
    def setupUi(self, choose_breakfast):
        choose_breakfast.setObjectName("choose_breakfast")
        choose_breakfast.resize(800, 600)
        font = QtGui.QFont()
        font.setFamily("楷体")
        font.setPointSize(14)
        choose_breakfast.setFont(font)
        icon = QtGui.QIcon()
        icon.addPixmap(QtGui.QPixmap("icon.svg"), QtGui.QIcon.Normal, QtGui.QIcon.Off)
        choose_breakfast.setWindowIcon(icon)
        self.breakfast_menu_list = QtWidgets.QListWidget(choose_breakfast)
        self.breakfast_menu_list.setGeometry(QtCore.QRect(10, 10, 381, 581))
        self.breakfast_menu_list.setObjectName("breakfast_menu_list")
        self.breakfast_recipe_list = QtWidgets.QListWidget(choose_breakfast)
        self.breakfast_recipe_list.setGeometry(QtCore.QRect(410, 10, 381, 391))
        self.breakfast_recipe_list.setObjectName("breakfast_recipe_list")
        self.label = QtWidgets.QLabel(choose_breakfast)
        self.label.setGeometry(QtCore.QRect(390, 420, 401, 121))
        self.label.setAlignment(QtCore.Qt.AlignCenter)
        self.label.setObjectName("label")
        self.cancel_button = QtWidgets.QPushButton(choose_breakfast)
        self.cancel_button.setGeometry(QtCore.QRect(580, 560, 91, 31))
        self.cancel_button.setObjectName("cancel_button")
        self.yes_button = QtWidgets.QPushButton(choose_breakfast)
        self.yes_button.setGeometry(QtCore.QRect(690, 560, 91, 31))
        self.yes_button.setObjectName("yes_button")

        self.retranslateUi(choose_breakfast)
        QtCore.QMetaObject.connectSlotsByName(choose_breakfast)

    def retranslateUi(self, choose_breakfast):
        _translate = QtCore.QCoreApplication.translate
        choose_breakfast.setWindowTitle(_translate("choose_breakfast", "选择早餐"))
        self.label.setText(_translate("choose_breakfast", "双击左侧列表项添加食物\n"
"双击右侧列表项移除食物\n"
"多份食物请多次双击添加"))
        self.cancel_button.setText(_translate("choose_breakfast", "取消"))
        self.yes_button.setText(_translate("choose_breakfast", "确定"))
```

##### choose_lunch

```python
from PyQt5 import QtCore, QtGui, QtWidgets


class Ui_choose_lunch(object):
    def setupUi(self, choose_lunch):
        choose_lunch.setObjectName("choose_lunch")
        choose_lunch.resize(800, 600)
        font = QtGui.QFont()
        font.setFamily("楷体")
        font.setPointSize(14)
        choose_lunch.setFont(font)
        icon = QtGui.QIcon()
        icon.addPixmap(QtGui.QPixmap("icon.svg"), QtGui.QIcon.Normal, QtGui.QIcon.Off)
        choose_lunch.setWindowIcon(icon)
        self.lunch_menu_list = QtWidgets.QListWidget(choose_lunch)
        self.lunch_menu_list.setGeometry(QtCore.QRect(10, 10, 381, 581))
        self.lunch_menu_list.setObjectName("lunch_menu_list")
        self.lunch_recipe_list = QtWidgets.QListWidget(choose_lunch)
        self.lunch_recipe_list.setGeometry(QtCore.QRect(410, 10, 381, 391))
        self.lunch_recipe_list.setObjectName("lunch_recipe_list")
        self.label = QtWidgets.QLabel(choose_lunch)
        self.label.setGeometry(QtCore.QRect(390, 420, 401, 121))
        self.label.setAlignment(QtCore.Qt.AlignCenter)
        self.label.setObjectName("label")
        self.cancel_button = QtWidgets.QPushButton(choose_lunch)
        self.cancel_button.setGeometry(QtCore.QRect(580, 560, 91, 31))
        self.cancel_button.setObjectName("cancel_button")
        self.yes_button = QtWidgets.QPushButton(choose_lunch)
        self.yes_button.setGeometry(QtCore.QRect(690, 560, 91, 31))
        self.yes_button.setObjectName("yes_button")

        self.retranslateUi(choose_lunch)
        QtCore.QMetaObject.connectSlotsByName(choose_lunch)

    def retranslateUi(self, choose_lunch):
        _translate = QtCore.QCoreApplication.translate
        choose_lunch.setWindowTitle(_translate("choose_lunch", "选择午餐"))
        self.label.setText(_translate("choose_lunch", "双击左侧列表项添加食物\n"
"双击右侧列表项移除食物\n"
"多份食物请多次双击添加"))
        self.cancel_button.setText(_translate("choose_lunch", "取消"))
        self.yes_button.setText(_translate("choose_lunch", "确定"))
```

##### choose_dinner

```python
from PyQt5 import QtCore, QtGui, QtWidgets


class Ui_choose_dinner(object):
    def setupUi(self, choose_dinner):
        choose_dinner.setObjectName("choose_dinner")
        choose_dinner.resize(800, 600)
        font = QtGui.QFont()
        font.setFamily("楷体")
        font.setPointSize(14)
        choose_dinner.setFont(font)
        icon = QtGui.QIcon()
        icon.addPixmap(QtGui.QPixmap("icon.svg"), QtGui.QIcon.Normal, QtGui.QIcon.Off)
        choose_dinner.setWindowIcon(icon)
        self.dinner_menu_list = QtWidgets.QListWidget(choose_dinner)
        self.dinner_menu_list.setGeometry(QtCore.QRect(10, 10, 381, 581))
        self.dinner_menu_list.setObjectName("dinner_menu_list")
        self.dinner_recipe_list = QtWidgets.QListWidget(choose_dinner)
        self.dinner_recipe_list.setGeometry(QtCore.QRect(410, 10, 381, 391))
        self.dinner_recipe_list.setObjectName("dinner_recipe_list")
        self.label = QtWidgets.QLabel(choose_dinner)
        self.label.setGeometry(QtCore.QRect(390, 420, 401, 121))
        self.label.setAlignment(QtCore.Qt.AlignCenter)
        self.label.setObjectName("label")
        self.cancel_button = QtWidgets.QPushButton(choose_dinner)
        self.cancel_button.setGeometry(QtCore.QRect(580, 560, 91, 31))
        self.cancel_button.setObjectName("cancel_button")
        self.yes_button = QtWidgets.QPushButton(choose_dinner)
        self.yes_button.setGeometry(QtCore.QRect(690, 560, 91, 31))
        self.yes_button.setObjectName("yes_button")

        self.retranslateUi(choose_dinner)
        QtCore.QMetaObject.connectSlotsByName(choose_dinner)

    def retranslateUi(self, choose_dinner):
        _translate = QtCore.QCoreApplication.translate
        choose_dinner.setWindowTitle(_translate("choose_dinner", "选择晚餐"))
        self.label.setText(_translate("choose_dinner", "双击左侧列表项添加食物\n"
"双击右侧列表项移除食物\n"
"多份食物请多次双击添加"))
        self.cancel_button.setText(_translate("choose_dinner", "取消"))
        self.yes_button.setText(_translate("choose_dinner", "确定"))
```

##### choose_gender

```python
from PyQt5 import QtCore, QtGui, QtWidgets


class Ui_choose_gender(object):
    def setupUi(self, choose_gender):
        choose_gender.setObjectName("choose_gender")
        choose_gender.resize(300, 70)
        font = QtGui.QFont()
        font.setFamily("楷体")
        font.setPointSize(10)
        choose_gender.setFont(font)
        icon = QtGui.QIcon()
        icon.addPixmap(QtGui.QPixmap("icon.svg"), QtGui.QIcon.Normal, QtGui.QIcon.Off)
        choose_gender.setWindowIcon(icon)
        self.horizontalLayoutWidget = QtWidgets.QWidget(choose_gender)
        self.horizontalLayoutWidget.setGeometry(QtCore.QRect(10, 10, 281, 51))
        self.horizontalLayoutWidget.setObjectName("horizontalLayoutWidget")
        self.horizontalLayout = QtWidgets.QHBoxLayout(self.horizontalLayoutWidget)
        self.horizontalLayout.setContentsMargins(10, 0, 10, 0)
        self.horizontalLayout.setSpacing(10)
        self.horizontalLayout.setObjectName("horizontalLayout")
        self.boy_button = QtWidgets.QPushButton(self.horizontalLayoutWidget)
        self.boy_button.setObjectName("boy_button")
        self.horizontalLayout.addWidget(self.boy_button)
        self.girl_button = QtWidgets.QPushButton(self.horizontalLayoutWidget)
        self.girl_button.setObjectName("girl_button")
        self.horizontalLayout.addWidget(self.girl_button)

        self.retranslateUi(choose_gender)
        QtCore.QMetaObject.connectSlotsByName(choose_gender)

    def retranslateUi(self, choose_gender):
        _translate = QtCore.QCoreApplication.translate
        choose_gender.setWindowTitle(_translate("choose_gender", "选择性别"))
        self.boy_button.setText(_translate("choose_gender", "我是男生"))
        self.girl_button.setText(_translate("choose_gender", "我是女生"))
```

##### firstpage

```python
# -*- coding: utf-8 -*-

# Form implementation generated from reading ui file 'firstpage.ui'
#
# Created by: PyQt5 UI code generator 5.15.9
#
# WARNING: Any manual changes made to this file will be lost when pyuic5 is
# run again.  Do not edit this file unless you know what you are doing.


from PyQt5 import QtCore, QtGui, QtWidgets


class Ui_firstpage(object):
    def setupUi(self, firstpage):
        firstpage.setObjectName("firstpage")
        firstpage.resize(400, 240)
        firstpage.setMinimumSize(QtCore.QSize(0, 0))
        icon = QtGui.QIcon()
        icon.addPixmap(QtGui.QPixmap("icon.svg"), QtGui.QIcon.Normal, QtGui.QIcon.Off)
        firstpage.setWindowIcon(icon)
        self.verticalLayout = QtWidgets.QVBoxLayout(firstpage)
        self.verticalLayout.setObjectName("verticalLayout")
        self.name_label = QtWidgets.QLabel(firstpage)
        font = QtGui.QFont()
        font.setFamily("楷体")
        font.setPointSize(28)
        self.name_label.setFont(font)
        self.name_label.setTextFormat(QtCore.Qt.PlainText)
        self.name_label.setObjectName("name_label")
        self.verticalLayout.addWidget(self.name_label)
        self.slogan_label = QtWidgets.QLabel(firstpage)
        font = QtGui.QFont()
        font.setFamily("宋体")
        font.setPointSize(12)
        self.slogan_label.setFont(font)
        self.slogan_label.setObjectName("slogan_label")
        self.verticalLayout.addWidget(self.slogan_label)
        self.newRecipe_Button = QtWidgets.QPushButton(firstpage)
        self.newRecipe_Button.setObjectName("newRecipe_Button")
        self.verticalLayout.addWidget(self.newRecipe_Button)
        self.Browse_Button = QtWidgets.QPushButton(firstpage)
        self.Browse_Button.setObjectName("Browse_Button")
        self.verticalLayout.addWidget(self.Browse_Button)

        self.retranslateUi(firstpage)
        QtCore.QMetaObject.connectSlotsByName(firstpage)

    def retranslateUi(self, firstpage):
        _translate = QtCore.QCoreApplication.translate
        firstpage.setWindowTitle(_translate("firstpage", "膳食助手"))
        self.name_label.setText(_translate("firstpage", "膳食助手"))
        self.slogan_label.setText(_translate("firstpage", "均衡营养，健康生活。"))
        self.newRecipe_Button.setText(_translate("firstpage", "新建食谱"))
        self.Browse_Button.setText(_translate("firstpage", "膳食指南"))
```

##### load_file

```python
# -*- coding: utf-8 -*-

# Form implementation generated from reading ui file 'load_file.ui'
#
# Created by: PyQt5 UI code generator 5.15.9
#
# WARNING: Any manual changes made to this file will be lost when pyuic5 is
# run again.  Do not edit this file unless you know what you are doing.


from PyQt5 import QtCore, QtGui, QtWidgets


class Ui_load_file(object):
    def setupUi(self, load_file):
        load_file.setObjectName("load_file")
        load_file.resize(400, 130)
        icon = QtGui.QIcon()
        icon.addPixmap(QtGui.QPixmap("icon.svg"), QtGui.QIcon.Normal, QtGui.QIcon.Off)
        load_file.setWindowIcon(icon)
        self.horizontalLayoutWidget = QtWidgets.QWidget(load_file)
        self.horizontalLayoutWidget.setGeometry(QtCore.QRect(20, 20, 361, 31))
        self.horizontalLayoutWidget.setObjectName("horizontalLayoutWidget")
        self.horizontalLayout = QtWidgets.QHBoxLayout(self.horizontalLayoutWidget)
        self.horizontalLayout.setContentsMargins(0, 0, 0, 0)
        self.horizontalLayout.setObjectName("horizontalLayout")
        self.path_label = QtWidgets.QLabel(self.horizontalLayoutWidget)
        font = QtGui.QFont()
        font.setFamily("宋体")
        font.setPointSize(10)
        self.path_label.setFont(font)
        self.path_label.setObjectName("path_label")
        self.horizontalLayout.addWidget(self.path_label)
        self.path_lineEdit = QtWidgets.QLineEdit(self.horizontalLayoutWidget)
        self.path_lineEdit.setObjectName("path_lineEdit")
        self.horizontalLayout.addWidget(self.path_lineEdit)
        self.file_Button = QtWidgets.QPushButton(self.horizontalLayoutWidget)
        self.file_Button.setObjectName("file_Button")
        self.horizontalLayout.addWidget(self.file_Button)
        self.cancel_button = QtWidgets.QPushButton(load_file)
        self.cancel_button.setGeometry(QtCore.QRect(220, 100, 75, 23))
        self.cancel_button.setObjectName("cancel_button")
        self.yes_button = QtWidgets.QPushButton(load_file)
        self.yes_button.setGeometry(QtCore.QRect(300, 100, 75, 23))
        self.yes_button.setObjectName("yes_button")
        self.boy_button = QtWidgets.QRadioButton(load_file)
        self.boy_button.setGeometry(QtCore.QRect(220, 60, 86, 31))
        self.boy_button.setChecked(True)
        self.boy_button.setObjectName("boy_button")
        self.girl_button = QtWidgets.QRadioButton(load_file)
        self.girl_button.setGeometry(QtCore.QRect(300, 60, 86, 31))
        self.girl_button.setObjectName("girl_button")

        self.retranslateUi(load_file)
        QtCore.QMetaObject.connectSlotsByName(load_file)

    def retranslateUi(self, load_file):
        _translate = QtCore.QCoreApplication.translate
        load_file.setWindowTitle(_translate("load_file", "导入文件"))
        self.path_label.setText(_translate("load_file", "文件路径"))
        self.file_Button.setText(_translate("load_file", "浏览..."))
        self.cancel_button.setText(_translate("load_file", "取消"))
        self.yes_button.setText(_translate("load_file", "确定"))
        self.boy_button.setText(_translate("load_file", "男生"))
        self.girl_button.setText(_translate("load_file", "女生"))
```

##### new_recipe

```python
from PyQt5 import QtCore, QtGui, QtWidgets


class Ui_new_recipe(object):
    def setupUi(self, new_recipe):
        new_recipe.setObjectName("new_recipe")
        new_recipe.resize(300, 400)
        font = QtGui.QFont()
        font.setFamily("楷体")
        font.setPointSize(12)
        new_recipe.setFont(font)
        icon = QtGui.QIcon()
        icon.addPixmap(QtGui.QPixmap("icon.svg"), QtGui.QIcon.Normal, QtGui.QIcon.Off)
        new_recipe.setWindowIcon(icon)
        self.verticalLayout = QtWidgets.QVBoxLayout(new_recipe)
        self.verticalLayout.setObjectName("verticalLayout")
        self.verticalLayout_2 = QtWidgets.QVBoxLayout()
        self.verticalLayout_2.setContentsMargins(50, 25, 50, 25)
        self.verticalLayout_2.setSpacing(15)
        self.verticalLayout_2.setObjectName("verticalLayout_2")
        self.choose_food_button = QtWidgets.QPushButton(new_recipe)
        self.choose_food_button.setObjectName("choose_food_button")
        self.verticalLayout_2.addWidget(self.choose_food_button)
        self.load_file_button = QtWidgets.QPushButton(new_recipe)
        self.load_file_button.setObjectName("load_file_button")
        self.verticalLayout_2.addWidget(self.load_file_button)
        self.first_page_button = QtWidgets.QPushButton(new_recipe)
        self.first_page_button.setObjectName("first_page_button")
        self.verticalLayout_2.addWidget(self.first_page_button)
        self.verticalLayout.addLayout(self.verticalLayout_2)

        self.retranslateUi(new_recipe)
        QtCore.QMetaObject.connectSlotsByName(new_recipe)

    def retranslateUi(self, new_recipe):
        _translate = QtCore.QCoreApplication.translate
        new_recipe.setWindowTitle(_translate("new_recipe", "新建食谱"))
        self.choose_food_button.setText(_translate("new_recipe", "选择食物"))
        self.load_file_button.setText(_translate("new_recipe", "从文件导入..."))
        self.first_page_button.setText(_translate("new_recipe", "返回"))
```

##### showpanel

```pyhon
from PyQt5 import QtCore, QtGui, QtWidgets


class Ui_showpanel(object):
    def setupUi(self, showpanel):
        showpanel.setObjectName("showpanel")
        showpanel.resize(800, 600)
        font = QtGui.QFont()
        font.setFamily("楷体")
        font.setPointSize(12)
        showpanel.setFont(font)
        icon = QtGui.QIcon()
        icon.addPixmap(QtGui.QPixmap("icon.svg"), QtGui.QIcon.Normal, QtGui.QIcon.Off)
        showpanel.setWindowIcon(icon)
        showpanel.setStyleSheet("background-color:qlineargradient(spread:pad, x1:0.5, y1:0, x2:0.5, y2:1, stop:0 rgba(79, 175, 255, 113), stop:1 rgba(255, 255, 255, 255))")
        self.verticalLayoutWidget = QtWidgets.QWidget(showpanel)
        self.verticalLayoutWidget.setGeometry(QtCore.QRect(20, 20, 761, 561))
        self.verticalLayoutWidget.setObjectName("verticalLayoutWidget")
        self.verticalLayout = QtWidgets.QVBoxLayout(self.verticalLayoutWidget)
        self.verticalLayout.setContentsMargins(0, 0, 0, 0)
        self.verticalLayout.setObjectName("verticalLayout")
        self.up_half = QtWidgets.QHBoxLayout()
        self.up_half.setObjectName("up_half")
        self.label = QtWidgets.QLabel(self.verticalLayoutWidget)
        self.label.setAutoFillBackground(False)
        self.label.setStyleSheet("background-color:rgba(170, 255, 255, 100)")
        self.label.setAlignment(QtCore.Qt.AlignCenter)
        self.label.setObjectName("label")
        self.up_half.addWidget(self.label)
        self.verticalLayout_5 = QtWidgets.QVBoxLayout()
        self.verticalLayout_5.setObjectName("verticalLayout_5")
        self.horizontalLayout_6 = QtWidgets.QHBoxLayout()
        self.horizontalLayout_6.setObjectName("horizontalLayout_6")
        self.label_2 = QtWidgets.QLabel(self.verticalLayoutWidget)
        self.label_2.setStyleSheet("background-color:rgba(170, 255, 255, 100)")
        self.label_2.setObjectName("label_2")
        self.horizontalLayout_6.addWidget(self.label_2)
        self.type_lcdnum = QtWidgets.QLCDNumber(self.verticalLayoutWidget)
        self.type_lcdnum.setStyleSheet("background-color:rgba(170, 255, 255, 100)")
        self.type_lcdnum.setSegmentStyle(QtWidgets.QLCDNumber.Flat)
        self.type_lcdnum.setProperty("value", 0.0)
        self.type_lcdnum.setObjectName("type_lcdnum")
        self.horizontalLayout_6.addWidget(self.type_lcdnum)
        self.verticalLayout_5.addLayout(self.horizontalLayout_6)
        self.horizontalLayout_8 = QtWidgets.QHBoxLayout()
        self.horizontalLayout_8.setObjectName("horizontalLayout_8")
        self.label_4 = QtWidgets.QLabel(self.verticalLayoutWidget)
        self.label_4.setStyleSheet("background-color:rgba(170, 255, 255, 100)")
        self.label_4.setObjectName("label_4")
        self.horizontalLayout_8.addWidget(self.label_4)
        self.variety_lcdnum = QtWidgets.QLCDNumber(self.verticalLayoutWidget)
        self.variety_lcdnum.setStyleSheet("background-color:rgba(170, 255, 255, 100)")
        self.variety_lcdnum.setSegmentStyle(QtWidgets.QLCDNumber.Flat)
        self.variety_lcdnum.setObjectName("variety_lcdnum")
        self.horizontalLayout_8.addWidget(self.variety_lcdnum)
        self.verticalLayout_5.addLayout(self.horizontalLayout_8)
        self.horizontalLayout_9 = QtWidgets.QHBoxLayout()
        self.horizontalLayout_9.setObjectName("horizontalLayout_9")
        self.label_5 = QtWidgets.QLabel(self.verticalLayoutWidget)
        self.label_5.setStyleSheet("background-color:rgba(170, 255, 255, 100)")
        self.label_5.setObjectName("label_5")
        self.horizontalLayout_9.addWidget(self.label_5)
        self.energy_intake_lcdnum = QtWidgets.QLCDNumber(self.verticalLayoutWidget)
        self.energy_intake_lcdnum.setStyleSheet("background-color:rgba(170, 255, 255, 100)")
        self.energy_intake_lcdnum.setSegmentStyle(QtWidgets.QLCDNumber.Flat)
        self.energy_intake_lcdnum.setObjectName("energy_intake_lcdnum")
        self.horizontalLayout_9.addWidget(self.energy_intake_lcdnum)
        self.verticalLayout_5.addLayout(self.horizontalLayout_9)
        self.horizontalLayout_12 = QtWidgets.QHBoxLayout()
        self.horizontalLayout_12.setObjectName("horizontalLayout_12")
        self.label_8 = QtWidgets.QLabel(self.verticalLayoutWidget)
        self.label_8.setStyleSheet("background-color:rgba(170, 255, 255, 100)")
        self.label_8.setObjectName("label_8")
        self.horizontalLayout_12.addWidget(self.label_8)
        self.breakfast_energy_lcdnum = QtWidgets.QLCDNumber(self.verticalLayoutWidget)
        self.breakfast_energy_lcdnum.setStyleSheet("background-color:rgba(170, 255, 255, 100)")
        self.breakfast_energy_lcdnum.setSegmentStyle(QtWidgets.QLCDNumber.Flat)
        self.breakfast_energy_lcdnum.setObjectName("breakfast_energy_lcdnum")
        self.horizontalLayout_12.addWidget(self.breakfast_energy_lcdnum)
        self.verticalLayout_5.addLayout(self.horizontalLayout_12)
        self.horizontalLayout_13 = QtWidgets.QHBoxLayout()
        self.horizontalLayout_13.setObjectName("horizontalLayout_13")
        self.label_9 = QtWidgets.QLabel(self.verticalLayoutWidget)
        self.label_9.setStyleSheet("background-color:rgba(170, 255, 255, 100)")
        self.label_9.setObjectName("label_9")
        self.horizontalLayout_13.addWidget(self.label_9)
        self.lunch_energy_lcd_num = QtWidgets.QLCDNumber(self.verticalLayoutWidget)
        self.lunch_energy_lcd_num.setStyleSheet("background-color:rgba(170, 255, 255, 100)")
        self.lunch_energy_lcd_num.setSegmentStyle(QtWidgets.QLCDNumber.Flat)
        self.lunch_energy_lcd_num.setObjectName("lunch_energy_lcd_num")
        self.horizontalLayout_13.addWidget(self.lunch_energy_lcd_num)
        self.verticalLayout_5.addLayout(self.horizontalLayout_13)
        self.horizontalLayout_7 = QtWidgets.QHBoxLayout()
        self.horizontalLayout_7.setObjectName("horizontalLayout_7")
        self.label_3 = QtWidgets.QLabel(self.verticalLayoutWidget)
        self.label_3.setStyleSheet("background-color:rgba(170, 255, 255, 100)")
        self.label_3.setObjectName("label_3")
        self.horizontalLayout_7.addWidget(self.label_3)
        self.dinner_energy_lcdnum = QtWidgets.QLCDNumber(self.verticalLayoutWidget)
        self.dinner_energy_lcdnum.setStyleSheet("background-color:rgba(170, 255, 255, 100)")
        self.dinner_energy_lcdnum.setSegmentStyle(QtWidgets.QLCDNumber.Flat)
        self.dinner_energy_lcdnum.setObjectName("dinner_energy_lcdnum")
        self.horizontalLayout_7.addWidget(self.dinner_energy_lcdnum)
        self.verticalLayout_5.addLayout(self.horizontalLayout_7)
        self.up_half.addLayout(self.verticalLayout_5)
        self.verticalLayout.addLayout(self.up_half)
        self.down_half = QtWidgets.QHBoxLayout()
        self.down_half.setObjectName("down_half")
        self.label_11 = QtWidgets.QLabel(self.verticalLayoutWidget)
        self.label_11.setStyleSheet("background-color:rgba(255, 170, 255, 70)")
        self.label_11.setAlignment(QtCore.Qt.AlignCenter)
        self.label_11.setObjectName("label_11")
        self.down_half.addWidget(self.label_11)
        self.verticalLayout_9 = QtWidgets.QVBoxLayout()
        self.verticalLayout_9.setObjectName("verticalLayout_9")
        self.horizontalLayout_18 = QtWidgets.QHBoxLayout()
        self.horizontalLayout_18.setObjectName("horizontalLayout_18")
        self.label_12 = QtWidgets.QLabel(self.verticalLayoutWidget)
        self.label_12.setStyleSheet("background-color:rgba(255, 170, 255, 70)")
        self.label_12.setObjectName("label_12")
        self.horizontalLayout_18.addWidget(self.label_12)
        self.verticalLayout_6 = QtWidgets.QVBoxLayout()
        self.verticalLayout_6.setObjectName("verticalLayout_6")
        self.horizontalLayout_10 = QtWidgets.QHBoxLayout()
        self.horizontalLayout_10.setObjectName("horizontalLayout_10")
        self.label_18 = QtWidgets.QLabel(self.verticalLayoutWidget)
        self.label_18.setStyleSheet("background-color:rgba(255, 170, 255, 70)")
        self.label_18.setObjectName("label_18")
        self.horizontalLayout_10.addWidget(self.label_18)
        self.carbon_lcdnum = QtWidgets.QLCDNumber(self.verticalLayoutWidget)
        self.carbon_lcdnum.setStyleSheet("background-color:rgba(255, 170, 255, 70)")
        self.carbon_lcdnum.setSegmentStyle(QtWidgets.QLCDNumber.Flat)
        self.carbon_lcdnum.setObjectName("carbon_lcdnum")
        self.horizontalLayout_10.addWidget(self.carbon_lcdnum)
        self.verticalLayout_6.addLayout(self.horizontalLayout_10)
        self.horizontalLayout = QtWidgets.QHBoxLayout()
        self.horizontalLayout.setObjectName("horizontalLayout")
        self.label_6 = QtWidgets.QLabel(self.verticalLayoutWidget)
        self.label_6.setStyleSheet("background-color:rgba(255, 170, 255, 70)")
        self.label_6.setObjectName("label_6")
        self.horizontalLayout.addWidget(self.label_6)
        self.fat_lcdnum = QtWidgets.QLCDNumber(self.verticalLayoutWidget)
        self.fat_lcdnum.setStyleSheet("background-color:rgba(255, 170, 255, 70)")
        self.fat_lcdnum.setSegmentStyle(QtWidgets.QLCDNumber.Flat)
        self.fat_lcdnum.setObjectName("fat_lcdnum")
        self.horizontalLayout.addWidget(self.fat_lcdnum)
        self.verticalLayout_6.addLayout(self.horizontalLayout)
        self.horizontalLayout_5 = QtWidgets.QHBoxLayout()
        self.horizontalLayout_5.setObjectName("horizontalLayout_5")
        self.label_16 = QtWidgets.QLabel(self.verticalLayoutWidget)
        self.label_16.setStyleSheet("background-color:rgba(255, 170, 255, 70)")
        self.label_16.setObjectName("label_16")
        self.horizontalLayout_5.addWidget(self.label_16)
        self.protein_lcdnum = QtWidgets.QLCDNumber(self.verticalLayoutWidget)
        self.protein_lcdnum.setStyleSheet("background-color:rgba(255, 170, 255, 70)")
        self.protein_lcdnum.setSegmentStyle(QtWidgets.QLCDNumber.Flat)
        self.protein_lcdnum.setObjectName("protein_lcdnum")
        self.horizontalLayout_5.addWidget(self.protein_lcdnum)
        self.verticalLayout_6.addLayout(self.horizontalLayout_5)
        self.horizontalLayout_18.addLayout(self.verticalLayout_6)
        self.verticalLayout_9.addLayout(self.horizontalLayout_18)
        self.horizontalLayout_19 = QtWidgets.QHBoxLayout()
        self.horizontalLayout_19.setObjectName("horizontalLayout_19")
        self.label_13 = QtWidgets.QLabel(self.verticalLayoutWidget)
        self.label_13.setStyleSheet("background-color:rgba(255, 170, 255, 70)")
        self.label_13.setObjectName("label_13")
        self.horizontalLayout_19.addWidget(self.label_13)
        self.non_lcdnum = QtWidgets.QLCDNumber(self.verticalLayoutWidget)
        self.non_lcdnum.setStyleSheet("background-color:rgba(255, 170, 255, 70)")
        self.non_lcdnum.setSegmentStyle(QtWidgets.QLCDNumber.Flat)
        self.non_lcdnum.setObjectName("non_lcdnum")
        self.horizontalLayout_19.addWidget(self.non_lcdnum)
        self.verticalLayout_9.addLayout(self.horizontalLayout_19)
        self.down_half.addLayout(self.verticalLayout_9)
        self.verticalLayout.addLayout(self.down_half)
        self.pushButton = QtWidgets.QPushButton(self.verticalLayoutWidget)
        self.pushButton.setStyleSheet("background-color:rgb(208, 208, 208)")
        self.pushButton.setObjectName("pushButton")
        self.verticalLayout.addWidget(self.pushButton)

        self.retranslateUi(showpanel)
        QtCore.QMetaObject.connectSlotsByName(showpanel)

    def retranslateUi(self, showpanel):
        _translate = QtCore.QCoreApplication.translate
        showpanel.setWindowTitle(_translate("showpanel", "膳食评价"))
        self.label.setText(_translate("showpanel", "膳食丰富度及能量摄入情况"))
        self.label_2.setText(_translate("showpanel", "膳食种类丰富度"))
        self.label_4.setText(_translate("showpanel", "食物多样性"))
        self.label_5.setText(_translate("showpanel", "能量总摄入(kJ)"))
        self.label_8.setText(_translate("showpanel", "早餐能量比%"))
        self.label_9.setText(_translate("showpanel", "午餐能量比%"))
        self.label_3.setText(_translate("showpanel", "晚餐能量比%"))
        self.label_11.setText(_translate("showpanel", "营养素摄入量评估"))
        self.label_12.setText(_translate("showpanel", "宏量营养素\n"
"供能占比%"))
        self.label_18.setText(_translate("showpanel", "碳水"))
        self.label_6.setText(_translate("showpanel", "脂肪"))
        self.label_16.setText(_translate("showpanel", "蛋白质"))
        self.label_13.setText(_translate("showpanel", "非产能营养素\n"
"摄入量偏离度"))
        self.pushButton.setText(_translate("showpanel", "均衡膳食，从我做起！（返回）"))
```

### 信号与槽函数的构建及与后端的连接

为了将UI设计与逻辑彻底分开，我们单独为页面设置一个类，将UI类进行导入，并在其上实现信号与槽函数的构建。

首先将页面导入到主文件中：

```python
from firstpage import Ui_firstpage as firstpage
from info import Ui_info as info
from load_file import Ui_load_file as load_file
from new_recipe import Ui_new_recipe as new_recipe
from showpanel import Ui_showpanel as showpanel
from choose_breakfast import Ui_choose_breakfast as choose_breakfast
from choose_lunch import Ui_choose_lunch as choose_lunch
from choose_dinner import Ui_choose_dinner as choose_dinner
from choose_gender import Ui_choose_gender as choose_gender
from evaluator import Evaluator
```

为每个页面添加信号与槽函数：

类的结构如下：

1. 跳转窗口的信号
2. 构造函数：
   - 父类初始化
   - setupUi
   - 将按键等组件与对应的函数连接
3. 与组件连接的函数
4. 与后端进行交互的函数

```python
class FirstpageWindow(QtWidgets.QWidget, firstpage):
    switch_window_new_recipe = QtCore.pyqtSignal() # 跳转信号
    switch_window_info = QtCore.pyqtSignal() # 跳转信号
    def __init__(self):
        super(FirstpageWindow, self).__init__()
        self.setupUi(self)
        self.newRecipe_Button.clicked.connect(self.goNew)
        self.Browse_Button.clicked.connect(self.goInfo)
    def goNew(self):
        self.switch_window_new_recipe.emit()
    def goInfo(self):
        self.switch_window_info.emit()
```

```python
class InfoWindow(QtWidgets.QWidget, info):
    def __init__(self):
        super(InfoWindow, self).__init__()
        self.setupUi(self)
```

```python
class LoadFileWindow(QtWidgets.QWidget, load_file):
    switch_window_new_recipe = QtCore.pyqtSignal()
    switch_window_showpanel = QtCore.pyqtSignal()
    def __init__(self):
        super(LoadFileWindow, self).__init__()
        self.file = None
        self.gender = "boy"
        self.setupUi(self)
        self.cancel_button.clicked.connect(self.goNewRecipe)
        self.yes_button.clicked.connect(self.goShowPanel)
        self.file_Button.clicked.connect(self.readFile)

    def goNewRecipe(self):
        self.switch_window_new_recipe.emit()

    def goShowPanel(self):
        self.switch_window_showpanel.emit()

    def readFile(self):
        #self指向自身，"Open File"为文件名，"./"为当前路径，最后为文件类型筛选器
        self.file,_ = QtWidgets.QFileDialog.getOpenFileName(self, "Open File", "./",
                                                            "Excel文件(*.xls;*.xlsx)")
        self.path_lineEdit.setText(self.file)
        self.update()

    def updateFile(self):
        # print(self.path_lineEdit.text())
        self.file = self.path_lineEdit.text()
        self.file = os.path.abspath(self.file)  # 这行代码很重要！Python和系统文件的路径标准有点差异，需要用os库进行调整
        # print("self.file in LoadFileWindow:")
        # print(self.file)

    def updateGender(self):
        if self.boy_button.isChecked():
            self.gender = "boy"
        elif self.girl_button.isChecked():
            self.gender = "girl"
        else:
            print(QtWidgets.QMessageBox.warning(self,"性别选择","请选择性别。", QtWidgets.QMessageBox.OK))
```

```python
class ShowPanelWindow(QtWidgets.QWidget, showpanel):
    switch_window_first_page = QtCore.pyqtSignal()
    def __init__(self, config):
        super(ShowPanelWindow, self).__init__()
        self.setupUi(self)
        self.pushButton.clicked.connect(self.goFirstpage)
        self.config = config

    def goFirstpage(self):
        self.switch_window_first_page.emit()

    def updateData(self):
        if self.config.recipe_path is None:
            recipe_dict = {**self.config.breakfast_recipe, **self.config.lunch_recipe, **self.config.dinner_recipe}
        else:
            try:
                df = pd.read_excel(self.config.recipe_path)
                recipe_dict = df.set_index('食物名称')[['餐次', '食用份数']].apply(tuple, axis=1).to_dict()
                evaluator = Evaluator(gender=self.config.gender)
                self.type_lcdnum.display(evaluator.type(recipe_dict))
                self.variety_lcdnum.display(evaluator.variety(recipe_dict))
                self.energy_intake_lcdnum.display(evaluator.energy(recipe_dict))
                a, b, c = evaluator.energy_proportion_list(recipe_dict)
                self.breakfast_energy_lcdnum.display(a)
                self.lunch_energy_lcd_num.display(b)
                self.dinner_energy_lcdnum.display(c)
                a, b, c = evaluator.m_energy_ratio_list(recipe_dict)
                self.carbon_lcdnum.display(a)
                self.fat_lcdnum.display(b)
                self.protein_lcdnum.display(c)
                self.non_lcdnum.display(evaluator.non_energy_nutrients(recipe_dict))
                self.update()
            except Exception as e:
                # 创建一个QMessageBox实例
                error_box = QtWidgets.QMessageBox()
                error_box.setIcon(QtWidgets.QMessageBox.Critical)
                error_box.setWindowTitle("读取数据错误")
                error_box.setText("读取Excel文件时发生错误，请检查文件是否符合要求。")
                icon = QtGui.QIcon()
                icon.addPixmap(QtGui.QPixmap("icon.svg"), QtGui.QIcon.Normal, QtGui.QIcon.Off)
                error_box.setWindowIcon(icon)
                # 显示消息框
                error_box.exec_()
```

```python
class ChooseBreakfastWindow(QtWidgets.QWidget, choose_breakfast):
    switch_window_new_recipe = QtCore.pyqtSignal()
    switch_window_choose_lunch = QtCore.pyqtSignal()
    def __init__(self, config):
        super(ChooseBreakfastWindow,self).__init__()
        self.config = config
        self.setupUi(self)
        self.cancel_button.clicked.connect(lambda: self.switch_window_new_recipe.emit())
        self.yes_button.clicked.connect(self.goChooseLunch)
        self.breakfast_menu_list.doubleClicked.connect(lambda: self.change_func(self.breakfast_menu_list))
        self.breakfast_recipe_list.doubleClicked.connect(lambda: self.change_func(self.breakfast_recipe_list))
        self.getMenuItems()

    def getMenuItems(self):
        df = pd.read_excel(self.config.breakfast_ref_path)
        for food in df["食物名称"]:
            item = QtWidgets.QListWidgetItem(food)
            self.breakfast_menu_list.addItem(item)

    def goChooseLunch(self):
        self.switch_window_choose_lunch.emit()
        self.commit_breakfast_recipe()

    def change_func(self, list):
        if list == self.breakfast_menu_list:
            item = QtWidgets.QListWidgetItem(self.breakfast_menu_list.currentItem())
            self.breakfast_recipe_list.addItem(item)
        else:
            self.breakfast_recipe_list.takeItem(self.breakfast_recipe_list.currentRow())

    def commit_breakfast_recipe(self):
        # 初始化一个空字典来存储食物及其对应的早餐类型和数量
        food_dict = {}

        # 遍历食物列表
        count = self.breakfast_recipe_list.count()
        for i in range(count):
            food = self.breakfast_recipe_list.item(i).text()
            # 假设所有食物默认都是早餐类型，您可以根据需要修改这个逻辑
            meal_type = "早餐"

            # 如果食物已经在字典中，则增加其数量
            if food in food_dict:
                food_dict[food] = (meal_type, food_dict[food][1] + 1)
            # 如果食物不在字典中，则添加到字典并设置数量为1
            else:
                food_dict[food] = (meal_type, 1)

        self.config.breakfast_recipe = food_dict
        print(self.config.breakfast_recipe)
```

```python
class ChooseLunchWindow(QtWidgets.QWidget, choose_lunch):
    switch_window_new_recipe = QtCore.pyqtSignal()
    switch_window_choose_dinner = QtCore.pyqtSignal()
    def __init__(self, config=None):
        super(ChooseLunchWindow,self).__init__()
        self.setupUi(self)
        self.config = config
        self.cancel_button.clicked.connect(lambda: self.switch_window_new_recipe.emit())
        self.yes_button.clicked.connect(self.goChooseDinner)
        self.lunch_menu_list.doubleClicked.connect(lambda: self.change_func(self.lunch_menu_list))
        self.lunch_recipe_list.doubleClicked.connect(lambda: self.change_func(self.lunch_recipe_list))
        self.getMenuItems()

    def getMenuItems(self):
        df = pd.read_excel(self.config.lunch_ref_path)
        for food in df["食物名称"]:
            item = QtWidgets.QListWidgetItem(food)
            self.lunch_menu_list.addItem(item)

    def goChooseDinner(self):
        self.switch_window_choose_dinner.emit()
        self.commit_lunch_recipe()

    def change_func(self, list):
        if list == self.lunch_menu_list:
            item = QtWidgets.QListWidgetItem(self.lunch_menu_list.currentItem())
            self.lunch_recipe_list.addItem(item)
        else:
            self.lunch_recipe_list.takeItem(self.lunch_recipe_list.currentRow())

    def commit_lunch_recipe(self):
        # 初始化一个空字典来存储食物及其对应的早餐类型和数量
        food_dict = {}

        # 遍历食物列表
        count = self.lunch_recipe_list.count()
        for i in range(count):
            food = self.lunch_recipe_list.item(i).text()
            # 假设所有食物默认都是早餐类型，您可以根据需要修改这个逻辑
            meal_type = "午餐"

            # 如果食物已经在字典中，则增加其数量
            if food in food_dict:
                food_dict[food] = (meal_type, food_dict[food][1] + 1)
            # 如果食物不在字典中，则添加到字典并设置数量为1
            else:
                food_dict[food] = (meal_type, 1)

        self.config.lunch_recipe = food_dict
        print(self.config.lunch_recipe)
```

```python
class ChooseDinnerWindow(QtWidgets.QWidget, choose_dinner):
    switch_window_new_recipe = QtCore.pyqtSignal()
    switch_window_choose_gender = QtCore.pyqtSignal()

    def __init__(self, config):
        super(ChooseDinnerWindow,self).__init__()
        self.config = config
        self.setupUi(self)
        self.dinner_menu_list.doubleClicked.connect(lambda: self.change_func(self.dinner_menu_list))
        self.dinner_recipe_list.doubleClicked.connect(lambda: self.change_func(self.dinner_recipe_list))
        self.getMenuItems()
        self.cancel_button.clicked.connect(lambda: self.switch_window_new_recipe.emit())
        self.yes_button.clicked.connect(self.goShowPanel)

    def getMenuItems(self):
        df = pd.read_excel(self.config.dinner_ref_path)
        for food in df["食物名称"]:
            item = QtWidgets.QListWidgetItem(food)
            self.dinner_menu_list.addItem(item)

    def goShowPanel(self):
        self.commit_dinner_recipe()
        self.switch_window_choose_gender.emit()

    def change_func(self, list):
        if list == self.dinner_menu_list:
            item = QtWidgets.QListWidgetItem(self.dinner_menu_list.currentItem())
            self.dinner_recipe_list.addItem(item)
        else:
            self.dinner_recipe_list.takeItem(self.dinner_recipe_list.currentRow())

    def commit_dinner_recipe(self):
        # 初始化一个空字典来存储食物及其对应的早餐类型和数量
        food_dict = {}

        # 遍历食物列表
        count = self.dinner_recipe_list.count()
        for i in range(count):
            food = self.dinner_recipe_list.item(i).text()
            # 假设所有食物默认都是早餐类型，您可以根据需要修改这个逻辑
            meal_type = "晚餐"

            # 如果食物已经在字典中，则增加其数量
            if food in food_dict:
                food_dict[food] = (meal_type, food_dict[food][1] + 1)
            # 如果食物不在字典中，则添加到字典并设置数量为1
            else:
                food_dict[food] = (meal_type, 1)

        self.config.dinner_recipe = food_dict
        print(self.config.dinner_recipe)
```

```python
class ChooseGenderWindow(QtWidgets.QWidget, choose_gender):
    switch_window_showpanel = QtCore.pyqtSignal()

    def __init__(self, config):
        super(ChooseGenderWindow, self).__init__()
        self.config = config
        self.setupUi(self)
        self.girl_button.clicked.connect(lambda:self.set_gender("girl"))
        self.boy_button.clicked.connect(lambda:self.set_gender("boy"))

    def set_gender(self, gender):
        self.config.gender = gender
        self.switch_window_showpanel.emit()
```

### 窗口间的切换与信息共享

为了方便窗口间的切换，设计了`Controller` 类对所有页面进行统一调度。为了将窗口间的信息进行共享，设计了`Config` 类来对程序运行时的参数和信息进行存储和传递。这样的方法可以使程序整体的层次较为清晰，各个模块各司其职，降低耦合。

#### `Config` 类的设计

将程序运行的参数进行统一记录，方便页面间的信息共享和未来程序的移植使用。

```python
class Config:
    def __init__(self):
        self.breakfast_ref_path = "output/canteen_breakfast_info.xlsx"
        self.lunch_ref_path = "output/canteen_lunch_info.xlsx"
        self.dinner_ref_path = "output/canteen_dinner_info.xlsx"
        self.recipe_path = None
        self.gender = None
        self.breakfast_recipe = None
        self.lunch_recipe = None
        self.dinner_recipe = None
```

#### `Controller` 类的设计

首先，将 `Config` 和各个页面的类导入到成员变量之中：

```python
    def __init__(self):
        self.config = Config()
        self.firstpage = FirstpageWindow()
        self.new_recipe = NewRecipeWindow()
        self.info = InfoWindow()
        self.load_file = LoadFileWindow()
        self.showpanel = ShowPanelWindow(config=self.config)
        self.choose_breakfast = ChooseBreakfastWindow(config=self.config)
        self.choose_lunch = ChooseLunchWindow(config=self.config)
        self.choose_dinner = ChooseDinnerWindow(config=self.config)
        self.choose_gender = ChooseGenderWindow(config=self.config)
```

接着，将各个页面的建构逻辑进行实现，包括页面发出的切换信号的处理：

```python
    def show_firstpage(self):
        self.firstpage.switch_window_new_recipe.connect(self.first_page2new_recipe)
        self.firstpage.switch_window_info.connect(self.first_page2info)
        self.firstpage.show()

    def show_info(self):
        self.info.show()

    def show_new_recipe(self):
        self.new_recipe.switch_window_load_file.connect(self.new_recipe2load_file)
        self.new_recipe.switch_window_first_page.connect(self.new_recipe2first_page)
        self.new_recipe.switch_window_choose_breakfast.connect(self.new_recipe2choose_breakfast)
        self.new_recipe.show()

    def show_load_file(self):
        self.load_file.switch_window_new_recipe.connect(self.load_file2new_recipe)
        self.load_file.switch_window_showpanel.connect(self.load_file2showpanel)
        self.load_file.show()

    def show_showpanel(self):
        self.showpanel.switch_window_first_page.connect(self.showpanel2first_page)
        self.showpanel.updateData()
        self.showpanel.show()

    def show_choose_breakfast(self):
        self.choose_breakfast.switch_window_new_recipe.connect(self.choose_breakfast2new_recipe)
        self.choose_breakfast.switch_window_choose_lunch.connect(self.choose_breakfast2choose_lunch)
        self.choose_breakfast.show()

    def show_choose_lunch(self):
        self.choose_lunch.switch_window_new_recipe.connect(self.choose_lunch2new_recipe)
        self.choose_lunch.switch_window_choose_dinner.connect(self.choose_lunch2choose_dinner)
        self.choose_lunch.show()

    def show_choose_dinner(self):
        self.choose_dinner.switch_window_new_recipe.connect(self.choose_dinner2new_recipe)
        self.choose_dinner.switch_window_choose_gender.connect(self.choose_dinner2choose_gender)
        self.choose_dinner.show()

    def show_choose_gender(self):
        self.choose_gender.switch_window_showpanel.connect(self.choose_gender2showpanel)
        self.choose_gender.show()
```

实现页面间切换的逻辑，包括之前页面是否保留，以及本页面获取信息的共享：

```python
    def first_page2new_recipe(self):
        self.firstpage.close()
        self.show_new_recipe()

    def first_page2info(self):
        self.show_info()

    def new_recipe2load_file(self):
        self.new_recipe.close()
        self.show_load_file()

    def new_recipe2first_page(self):
        self.new_recipe.close()
        self.show_firstpage()

    def new_recipe2choose_breakfast(self):
        self.new_recipe.close()
        self.show_choose_breakfast()

    def load_file2new_recipe(self):
        self.load_file.close()
        self.show_new_recipe()

    def load_file2showpanel(self):
        self.load_file.updateFile()
        self.config.recipe_path = self.load_file.file
        self.load_file.updateGender()
        self.config.gender = self.load_file.gender
        self.load_file.close()
        self.show_showpanel()

    def showpanel2first_page(self):
        self.showpanel.close()
        self.show_firstpage()

    def choose_breakfast2new_recipe(self):
        self.choose_breakfast.close()
        self.show_new_recipe()

    def choose_lunch2new_recipe(self):
        self.choose_lunch.close()
        self.show_new_recipe()

    def choose_dinner2new_recipe(self):
        self.choose_dinner.close()
        self.show_new_recipe()

    def choose_breakfast2choose_lunch(self):
        self.choose_breakfast.close()
        self.show_choose_lunch()

    def choose_lunch2choose_dinner(self):
        self.choose_lunch.close()
        self.show_choose_dinner()

    def choose_dinner2choose_gender(self):
        self.choose_dinner.close()
        self.show_choose_gender()

    def choose_gender2showpanel(self):
        self.choose_gender.close()
        self.show_showpanel()
```

### 实验运行和结果

```python
def main():
    app = QtWidgets.QApplication(sys.argv)
    controller = Controller() # 控制器实例
    controller.show_firstpage()
    sys.exit(app.exec_())

if __name__ == '__main__':
    main()
```

各功能均可正常运行，并可以对不符合要求的表格输入等情况进行错误信息的反馈，并可以等待用户进行重试，程序具有较强的鲁棒性。

<img src="F:\repo-of-Flo\school curriculums\PythonExperiments\Python程序设计综合训练实验报告.assets\image-20240606181230683.png" alt="image-20240606181230683" style="zoom:50%;" />

## 实验过程中遇到的问题和解决过程

### 安装pandas时的版本问题

参考书中的一些操作已经不能被最新的pandas所支持，导致代码不断报错。

通过查阅资料发现是版本的问题，因此安装了稍微旧一些的版本。在安装时还遇到了与python版本不兼容的问题，于是在anaconda中新建了虚拟环境，使用旧版本的python进行实验。

之后直接查阅新的替代命令，更加“优雅”地解决了版本的问题。

### 表格处理的问题

在网络上找到的表格有些值比较奇怪，在使用代码进行处理时虽然逻辑大致正确，但是总会出现一些意想不到的结果，于是通过细致地分析对表格进行了多种办法的预处理。

另外，在处理合并单元格时也遇到了不少障碍，有些需要前向填充，有些需要字符串连接，还有一些需要判断是否相等，并留下符合条件的值。这个问题通过将列分别使用不同的填充策略进行了解决。

### python文件路径与Windows资源管理器文件路径格式不同

在通过导入文件来导入食谱时，总是遇到奇怪的报错，排查之后才发现是由于python文件路径的斜杠与windows的斜杠方向是相反的。

这个问题通过 `self.file = os.path.abspath(self.file)` 进行了解决。

### 切换页面和页面间信息共享方案的设计

在刚开始面对这个问题时，曾尝试通过页面间的相互调用来实现这个功能，但是随着页面越来越多，代码的结构越来越混乱，编程越来越痛苦。

最终通过查阅资料参考了 `Controller` 的设计方式，将代码结构变得更加有条理。

## 思考与感悟

在开发膳食助手软件过程中，我结合`pandas` 处理数据，完成繁杂的数据分析任务。`PyQt` 的使用，则让软件界面友好且功能丰富，实现了GUI的无缝对接。采取面向对象编程，代码结构清晰，便于维护和扩展。而坚持前后端分离原则，使得开发和调试工作更有条理，能迅速响应需求变化。这次实践不仅技术上收获颇丰，也让我深刻理解了以用户为中心的设计思维。

在开发小工具的过程中，我也感受到了软件开发的流程。与算法的编程不同，从数据收集到数据处理，从需求分析到界面设计、功能的实现和不断迭代，整个流程更加有章法，有了更多“工程”的概念。