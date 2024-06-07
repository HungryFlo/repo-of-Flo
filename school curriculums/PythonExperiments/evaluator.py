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

    def energy(self,recipe_dict):
        result_df = self.calculate_nutrition(recipe_dict)
        intake = result_df.loc["sum"]['能量（千卡）'].item()
        return float(intake)

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
        # print(assessment)

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


if __name__ == '__main__':
    df = pd.read_excel("excel/diet_boy.xlsx")
    recipe_dict = df.set_index('食物名称')[['餐次', '食用份数']].apply(tuple, axis=1).to_dict()
    print(recipe_dict)
    evaluator = Evaluator(gender="boy")
    print(evaluator.type(recipe_dict))
    print(evaluator.variety(recipe_dict))
    # print(evaluator.variety_details(recipe_dict))
    print(evaluator.energy(recipe_dict))
    # print(evaluator.energy_every_meal(recipe_dict))
    print(evaluator.energy_proportion_list(recipe_dict))
    print(evaluator.m_energy_ratio_list(recipe_dict))
    print(evaluator.non_energy_nutrients(recipe_dict))
    # print(evaluator.price(recipe_dict))
    # print(evaluator.aas(recipe_dict))
    # evaluator.calculate_nutrition(recipe_dict).to_excel("output/diet_calc_boy.xlsx", index=False)
    # print(evaluator.grey_score(recipe_dict))