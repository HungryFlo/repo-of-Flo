import numpy as np
import pandas as pd
import warnings
warnings.filterwarnings('ignore')

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
# ===================================================================

# ============================预处理==============================
def clean_and_convert(code):
    cleaned_code = ''.join(filter(str.isdigit, str(code)))  # 移除字母，只保留数字
    if cleaned_code:  # 确保处理后的字符串不为空
        return int(cleaned_code)  # 将干净的字符串转换为整数
    else:
        return np.nan  # 如果转换后为空字符串，则返回NaN表示无法转换
# 应用该函数到'食物编码'列
canteen_foods_df['食物编码'] = canteen_foods_df['食物编码'].apply(clean_and_convert)
canteen_foods_df.loc[canteen_foods_df['主要成分'] == '土豆', '食物编码'] = 21101

merge_df = pd.merge(canteen_foods_df, nutrition_df, on="食物编码", how='left')
merge_df = pd.merge(merge_df,aa_df, on="主要成分", how='left')
# print(merge_df.head())

# 定义一个函数，用于修改列名
def add_unit_to_acid_cols(column_name):
    if column_name.endswith('酸'):
        return column_name + '（毫克/克蛋白质）'
    else:
        return column_name
# 应用函数到DataFrame的列名
new_column_names = {col: add_unit_to_acid_cols(col) for col in merge_df.columns}
merge_df.rename(columns=new_column_names, inplace=True)

canteen_foods_df = merge_df

# 定义一个函数来处理列名
def clean_column_name(name):
    name = name.replace('\n', '')  # 移除换行符
    if '_x'  in name:
        name = name.replace('_x', '')
    name = name.replace('(', '（')  # 将左括号替换为全角括号
    return name
# 使用字典映射和列表推导式来创建新的列名列表
new_columns = {old: clean_column_name(old) for old in canteen_foods_df.columns}
# 用新列名替换原DataFrame的列名
canteen_foods_df.rename(columns=new_columns, inplace=True)
# 删除无用的列
canteen_foods_df.drop(['食物名称_y', '备注,标明食物或数据的来源', '可食部分%'], axis=1, inplace=True)

# 对多种成分的食物进行处理
# 指定需要进行前向填充的列
columns_to_fill = ['序号', '食物名称', '价格（元/份）','是否可半份']
# 仅对指定列进行前向填充
canteen_foods_df[columns_to_fill] = canteen_foods_df[columns_to_fill].fillna(method='ffill')

# 将无数据的值赋值为0
columns_not_to_fill_zero = ['序号', '食物名称', '主要成分', '食物编码', '可食部（克/份）', '价格（元/份）', '是否可半份']
columns_to_fill_zero = list(set(canteen_foods_df.columns).difference(set(columns_not_to_fill_zero)))
canteen_foods_df[columns_to_fill_zero] = canteen_foods_df[columns_to_fill_zero].fillna(0)
# ===================================================================

# ============添加每种食物的种类信息=====================
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
# ===================================================================

# ==================计算各食品最小售卖单元的营养物质信息======================
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

canteen_foods_df.to_excel(output_path[id], index=False)