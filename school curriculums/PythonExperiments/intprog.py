import random
from evaluator import Evaluator
import pandas as pd
import numpy as np
from collections import defaultdict


import warnings
warnings.filterwarnings('ignore')

def init_choice(meal):
    if meal == "早餐":
        return np.zeros(breakfast_foods_num, dtype=np.int32)
    elif meal == "午餐":
        return np.zeros(lunch_foods_num)
    else:
        return np.zeros(dinner_foods_num)

def get_time(x):
    if len(x) == 33:
        return "早餐"
    elif len(x) == 59:
        return "午餐"
    elif len(x) == 49:
        return "晚餐"
    else:
        return "错误！"

def get_recipe(x):
    meal = get_time(x)


def judge_day(recipe_dict, evaluator):
    # print(recipe_dict)
    if (evaluator.type(recipe_dict) >= 5
        and evaluator.variety(recipe_dict) > 12
        and evaluator.energy(recipe_dict) >= 2160 if evaluator.gender=="boy" else 1710
        and evaluator.energy(recipe_dict) <= 2640 if evaluator.gender=="boy" else 2090
        and evaluator.energy_proportion(recipe_dict)["早餐"] >= 0.25
        and evaluator.energy_proportion(recipe_dict)["早餐"] <= 0.35
        and evaluator.energy_proportion(recipe_dict)["午餐"] >= 0.3
        and evaluator.energy_proportion(recipe_dict)["午餐"] <= 0.4
        and evaluator.energy_proportion(recipe_dict)["晚餐"] >= 0.3
        and evaluator.energy_proportion(recipe_dict)["晚餐"] <= 0.4
        and evaluator.m_energy_ratio(recipe_dict)["protein"] >= 0.1
        and evaluator.m_energy_ratio(recipe_dict)["protein"] <= 0.15
        and evaluator.m_energy_ratio(recipe_dict)["fat"] >= 0.2
        and evaluator.m_energy_ratio(recipe_dict)["fat"] <= 0.3
        and evaluator.m_energy_ratio(recipe_dict)["carbon"] <= 0.65
        and evaluator.m_energy_ratio(recipe_dict)["carbon"] >= 0.5
    ):
        return True
    else:
        return False

def judge_week(recipe_dict_list, evaluator):
    variety_set = set()
    for recipe_dict in recipe_dict_list:
        if judge_day(recipe_dict,evaluator):
            variety_set = variety_set | (evaluator.variety_details(recipe_dict))
    if len(variety_set) >= 25:
        return True
    else:
        return False


# 读入食物表
breakfast_info = pd.read_excel("nutrition_per_serving_breakfast.xlsx")
lunch_info = pd.read_excel("nutrition_per_serving_lunch.xlsx")
dinner_info = pd.read_excel("nutrition_per_serving_dinner.xlsx")

# 查看各个食物表的食物种数
breakfast_foods_num = len(breakfast_info)
lunch_foods_num = len(lunch_info)
dinner_foods_num = len(dinner_info)

# 目标函数
def npNutritionLoss(recipe_dict, evaluator):
    return evaluator.non_energy_nutrients(recipe_dict)

def aas(recipe_dict, evaluator):
    return evaluator.aas(recipe_dict)

def price(recipe_dict, evaluator):
    return evaluator.price(recipe_dict)

def choose(choice_list):
    num = len(choice_list)
    times = random.randint(0, num // 2)
    for i in range(times):
        choice_list[random.randint(0, num-1)] += 1
    return choice_list


# ======================蒙特卡洛===================
# 初始化计数器
cnt = 0
cnt_2 = 0

# 初始化结果字典和记录极值对应食谱的字典
result_dict = {
    'Nutrition_Loss_min': float('inf'),
    'Nutrition_Loss_max': float('-inf'),
    'AAS_min': float('inf'),
    'AAS_max': float('-inf'),
    'Price_min': float('inf'),
    'Price_max': float('-inf'),
    'Score_max': float('-inf')
}
best_recipes = defaultdict(list)  # 用于存储达到极值时的食谱

result_list = []

while True:
    cnt_2 += 1
    evaluator = Evaluator(gender="boy")

    # 初始化食物选择向量
    breakfast_choice = np.zeros(breakfast_foods_num, dtype=np.int32)  # 不知道要不要加这个
    lunch_choice = np.zeros(lunch_foods_num)
    dinner_choice = np.zeros(dinner_foods_num)
    breakfast_choice = choose(breakfast_choice)
    lunch_choice = choose(lunch_choice)
    dinner_choice = choose(dinner_choice)

    # 形成食谱
    # 先在表里找到对应的食物名称，下次要注意尽量全都编号化
    breakfast_foods_list = breakfast_info["食物名称"].tolist()
    lunch_foods_list = lunch_info["食物名称"].tolist()
    dinner_foods_list = dinner_info["食物名称"].tolist()
    breakfast_recipe = {key: ("早餐", value) for key, value in zip(breakfast_foods_list, breakfast_choice)}
    lunch_recipe = {key: ("午餐", value) for key, value in zip(lunch_foods_list, lunch_choice)}
    dinner_recipe = {key: ("晚餐", value) for key, value in zip(dinner_foods_list, dinner_choice)}

    # 开始评估
    recipe_dict = dict(breakfast_recipe)
    recipe_dict.update(lunch_recipe)
    recipe_dict.update(dinner_recipe)

    # 评估逻辑...
    if not judge_day(recipe_dict, evaluator):
        continue
    else:
        # 更新极值并记录食谱
        nutrition_loss = npNutritionLoss(recipe_dict, evaluator)
        aas_value = aas(recipe_dict, evaluator)
        price_value = price(recipe_dict, evaluator)

        # 记录极小值
        if nutrition_loss <= result_dict['Nutrition_Loss_min']:
            result_dict['Nutrition_Loss_min'] = nutrition_loss
            best_recipes['Nutrition_Loss_min'].append((recipe_dict.copy(), evaluator))
        if aas_value <= result_dict['AAS_min']:
            result_dict['AAS_min'] = aas_value
            best_recipes['AAS_min'].append((recipe_dict.copy(), evaluator))
        if price_value <= result_dict['Price_min']:
            result_dict['Price_min'] = price_value
            best_recipes['Price_min'].append((recipe_dict.copy(), evaluator))

        # 记录极大值
        if nutrition_loss >= result_dict['Nutrition_Loss_max']:
            result_dict['Nutrition_Loss_max'] = nutrition_loss
            best_recipes['Nutrition_Loss_max'].append((recipe_dict.copy(), evaluator))
        if aas_value >= result_dict['AAS_max']:
            result_dict['AAS_max'] = aas_value
            best_recipes['AAS_max'].append((recipe_dict.copy(), evaluator))
        if price_value >= result_dict['Price_max']:
            result_dict['Price_max'] = price_value
            best_recipes['Price_max'].append((recipe_dict.copy(), evaluator))
        if price_value >= result_dict['Score_max']:
            result_dict['Score_max'] = price_value
            best_recipes['Score_max'].append((recipe_dict.copy(), evaluator))

        cnt += 1

        # 打印进度
        if cnt in [1, 2, 3, 100, 150, 200, 250, 300, 350, 400, 450, 500, 550, 600, 650, 700] or cnt >= 800:
            print(f"{cnt}!")
            print(result_dict)
            print(f"Total iterations: {cnt_2}")

        if cnt >= 800:
            break

# 输出最终结果及最佳食谱
print("Final results:")
print(result_dict)
print("\nBest recipes for each metric:")
for key, recipes in best_recipes.items():
    print(f"{key}:")
    for idx, (recipe, _) in enumerate(recipes, start=1):
        print(f"  Recipe {idx}: {recipe}")
# ===================蒙特卡洛==================






