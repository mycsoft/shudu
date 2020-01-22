/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package name.myc.sudu;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 *
 * @author mayic
 */
public class SudoMain {

    public static void main(String[] args) {
        //加载原始数据
        Table t = Table.createBySingle(Arrays.asList(
                0, 1, 0, 4, 0, 9, 0, 8, 0,
                4, 0, 0, 7, 2, 5, 0, 0, 9,
                0, 0, 0, 0, 0, 0, 0, 0, 0,
                3, 5, 0, 0, 0, 0, 0, 6, 8,
                0, 9, 0, 0, 0, 0, 0, 1, 0,
                2, 7, 0, 0, 0, 0, 0, 4, 3,
                0, 0, 0, 0, 0, 0, 0, 0, 0,
                9, 0, 0, 5, 4, 1, 0, 0, 6,
                0, 6, 0, 3, 0, 8, 0, 9, 0
        ));

//计算
        //显示计算结果。
    }

    /**
     * 单元格
     */
    static class Cell {

        int row;
        int column;
        int value;
        List<Integer> p;

        public Cell(int value) {
            this(0, 0, value);
        }

        public Cell(int row, int column, int value) {
            this(row, column, value, null);
        }

        public Cell(int row, int column, int value, List<Integer> p) {
            this.row = row;
            this.column = column;
            this.value = value;
            this.p = p;
        }

    };

    /**
     * 集合，一行，一列，或一个正文区。
     */
    static class Region {

        List<Cell> cells;
    }

    /**
     * 主表格。
     */
    static class Table {

        static final int MAX_ROW = 9;
        static final int MAX_COLUMN = 9;

        Cell[][] cells = new Cell[MAX_ROW][MAX_COLUMN];
        //未知的数量.
        int unkonwn = 0;

        /**
         * 根据单行的数据进行创建Table.
         *
         * @param cells
         * @return
         */
        static Table createBySingle(List<Integer> number) {
            Table t = new Table();
            for (int i = 0; i < number.size(); i++) {
                int v = number.get(i);
                //一行9个，逐行进行加载
                int row = i % MAX_ROW;
                int column = i % MAX_COLUMN;
                //加载时记录行列号.
                Cell cell = new Cell(row, column, v);
                t.cells[row][column] = cell;
                if (v == 0) {
                    t.unkonwn++;
                }
            }
            return t;
        }
    }
}
