package sudoku;

import java.util.Random;

/**
 * 定义Puzzle类型，用来表达和一局游戏相关的信息。
 */
public class Puzzle {
    //numbers数组存储一局游戏的数字分布
    int[][] numbers = new int[SudokuConstants.GRID_SIZE][SudokuConstants.GRID_SIZE];
    // isGiven数组用来存储一局游戏单元格的数字是否暴露的状态
    boolean[][] isGiven = new boolean[SudokuConstants.GRID_SIZE][SudokuConstants.GRID_SIZE];

    private boolean check(int[][] numbers, int newNum, int row, int col) {
        for(int i=0; i < 9; i++) {
            if(numbers[row][i]==newNum) return false;
            if(numbers[i][col]==newNum) return false;
        }
        int gridRow = row / 3;
        int gridCol = col / 3;
        for(int i = gridRow * 3; i < gridRow * 3 + 3; i++) {
            for(int j = gridCol * 3; j < gridCol * 3 + 3; j++) {
                if(numbers[i][j]==newNum) return false;
            }
        }
        return true;
    }

    // 根据设定需要猜测的单元个数新生成一局独数
    // 可以利用猜测的单元个数的多少做为游戏难度级别的设定依据
    // 这个方法需要对numbers数组和isGiven数组进行更新
    public void newPuzzle(int cellsToGuess) {
        //随机产生新的数独盘面
        int[][] nums = new int[9][9];
        // 填入数字
        Random rand = new Random();
        int newNum;
        newNum = rand.nextInt(9) + 1;
        for(int i=0; i<9; i++) {
            for(int j=0; j<9; j++) {
                int cnt = 0;
                while(true) {
                    if(check(nums,newNum,i,j)) {
                        nums[i][j] = newNum;
                        break;
                    } else {
                        newNum = newNum % 9 + 1;
                        cnt++;
                        if(cnt > 9) {
                            break;
                        }
                    }
                }
            }
            newNum = newNum % 9 + 1;
        }

        // 对数独进行变换
        int exchangeTimes = 25;
        for(int i=0; i < exchangeTimes; i++) {
            // 在组内交换两行
            int row1 = rand.nextInt(9);
            int difr = rand.nextInt(2);
            int row2 = (row1 / 3) * 3 + (row1 % 3 + difr) % 3;
            int[] tempr = nums[row1];
            nums[row1] = nums[row2];
            nums[row2] = tempr;
            // 在组内交换两列
            int col1 = rand.nextInt(9);
            int difc = rand.nextInt(2);
            int col2 = (col1 / 3) * 3 + (col1 % 3 + difc) % 3;
            for (int j = 0; j < 9; j++) {
                int tempc = nums[j][col1];
                nums[j][col1] = nums[j][col2];
                nums[j][col2] = tempc;
            }
            if (i % 10 == 0) {
                // 在组内交换两行
                row1 = rand.nextInt(9);
                difr = rand.nextInt(2);
                row2 = (row1 / 3) * 3 + (row1 % 3 + difr) % 3;
                tempr = nums[row1];
                nums[row1] = nums[row2];
                nums[row2] = tempr;
            } else if (i % 7 == 0) {
                // 在组内交换两列
                col1 = rand.nextInt(9);
                difc = rand.nextInt(2);
                col2 = (col1 / 3) * 3 + (col1 % 3 + difc) % 3;
                for (int j = 0; j < 9; j++) {
                    int tempc = nums[j][col1];
                    nums[j][col1] = nums[j][col2];
                    nums[j][col2] = tempc;
                }
            }
        }

        for (int row = 0; row < SudokuConstants.GRID_SIZE; ++row) {
            for (int col = 0; col < SudokuConstants.GRID_SIZE; ++col) {
                numbers[row][col] = nums[row][col];
            }
        }

        // 设置挖空的数字
        boolean[][] numbersIsGiven = new boolean[9][9];
        for(int i=0; i<9; i++) {
            for(int j=0; j<9; j++) {
                numbersIsGiven[i][j] = true;
            }
        }
        int numOfUnknown = 0;
        while(numOfUnknown < cellsToGuess) {
            int unknownRow = rand.nextInt(9);
            int unknownCol = rand.nextInt(9);
            if(numbersIsGiven[unknownRow][unknownCol]) {
                numbersIsGiven[unknownRow][unknownCol] = false;
                numOfUnknown++;
            }
        }

        // 可以使用cellsToGuess的值初始化isGiven数组中false的数量，即需要猜测的单元格数量
        // hardcodedIsGiven是预先设定的有哪些位置的单元格被暴露（在下面的数据中只指定暴露2个单元格）
        for (int row = 0; row < SudokuConstants.GRID_SIZE; ++row) {
            for (int col = 0; col < SudokuConstants.GRID_SIZE; ++col) {
                isGiven[row][col] = numbersIsGiven[row][col];
            }
        }
    }
}