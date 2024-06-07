import os.path
import sys
from PyQt5 import QtWidgets, QtCore, QtGui
import pandas as pd

# 导入 Qt designer 设计的页面
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

# 主窗口
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

# 登录窗口
class NewRecipeWindow(QtWidgets.QWidget, new_recipe):
    switch_window_choose_breakfast = QtCore.pyqtSignal()  # 跳转信号
    switch_window_load_file = QtCore.pyqtSignal()  # 跳转信号
    switch_window_first_page = QtCore.pyqtSignal()  # 跳转信号
    def __init__(self):
        super(NewRecipeWindow, self).__init__()
        self.setupUi(self)
        self.choose_food_button.clicked.connect(self.goChooseBreakfast)
        self.load_file_button.clicked.connect(self.goLoadFile)
        self.first_page_button.clicked.connect(self.goFirstpage)

    def goFirstpage(self):
        self.switch_window_first_page.emit()

    def goChooseBreakfast(self):
        self.switch_window_choose_breakfast.emit()

    def goLoadFile(self):
        self.switch_window_load_file.emit()

class InfoWindow(QtWidgets.QWidget, info):
    def __init__(self):
        super(InfoWindow, self).__init__()
        self.setupUi(self)

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

# 利用一个控制器来控制页面的跳转
class Controller:
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

    # 各个页面的建构逻辑
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

    # 页面切换函数
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


def main():
    app = QtWidgets.QApplication(sys.argv)
    controller = Controller() # 控制器实例
    controller.show_firstpage()
    sys.exit(app.exec_())

if __name__ == '__main__':
    main()