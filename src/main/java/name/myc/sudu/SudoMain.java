/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package name.myc.sudu;

import java.io.PrintStream;
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
        System.out.println(String.format("加载成功,需要计算%d个数字.", t.unkonwn));
        //计算
        t.compute();
        System.out.println(String.format("计算结束,还有%d个数字没有计算出来.", t.unkonwn));
        //显示计算结果。
        System.out.println("===============================================");
        t.printAll();
    }

    /**
     * 单元格.
     *
     * 为防止循环调用,所有组装方法,只从下向上组装.
     */
    static class Cell {

        int row;
        int column;
        int value;
        List<Integer> p;
        Region pile;
        Region verticalLine;
        Region horizontalLine;

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

        private void putToRow(Region rowReg) {
            throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
        }

        private void putToColumn(Region column) {
            throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
        }

        /**
         * 安装到堆中.
         *
         * @param pile
         */
        private void putToPile(Region pile) {

            throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
        }

        private void printResult(PrintStream out) {
            throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
        }

    };

    /**
     * 集合，一行，一列，或一个正文区。
     */
    static class Region {

        private static Region create(int MAX_COLUMN) {
            throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
        }

        List<Cell> cells;
        PileLine verticalLine;
        PileLine horizontalLine;
    }

    /**
     * 堆线.
     *
     * 用于计算三个同行(或同列)方阵之间的数字排除关系. 一般一个堆线为3*1; 用
     */
    static class PileLine {

        List<Region> piles;
        Table parent;
    }

    /**
     * 主表格。
     */
    static class Table {

        static final int MAX_ROW = 9;
        static final int MAX_COLUMN = 9;
        static final int PILE_ROW_COUNT = 3;
        static final int PILE_COLUMN_COUNT = 3;
        static final int PILE_LINE_ROW_COUNT = 3;
        static final int PILE_LINE_COLUMN_COUNT = 3;

        Cell[][] cells = new Cell[MAX_ROW][MAX_COLUMN];

        List<Region> rows;
        List<Region> columns;
        /**
         * 堆.
         */
        List<Region> piles;

        List<PileLine> verticalPiles;
        List<PileLine> horizontalPiles;

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

        /**
         * 计算结果。
         */
        private void compute() {
            //初始化计算模型
            initComputeModel();
            //暴力算法：计算每一个单元格的可能值。
            simpleCompute();

        }

        private void printAll() {
            for (int i = 0; i < cells.length; i++) {
                Cell[] row = cells[i];
                for (int j = 0; j < row.length; j++) {
                    Cell c = row[j];
                    System.out.print("\t|");
                    c.printResult(System.out);
                }
                System.out.println("\t|");
            }
        }

        /**
         * 简单粗暴的算法。
         */
        private void simpleCompute() {
            //遍历每个cell,记录可能值。直到只有一个可能性。
            for (Cell[] cell : cells) {

            }
            throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
        }

        /**
         * 初始化计算模型。
         */
        private void initComputeModel() {
            //初始化每一行
//            rows = new ArrayList<>(MAX_ROW);
            rows = createRegionList(MAX_ROW);
            //初始化每一列
//            columns = new ArrayList<>(MAX_COLUMN);
            columns = createRegionList(MAX_COLUMN);

            //初始化每一个子区域
            initPiles();

            for (int i = 0; i < cells.length; i++) {
                Cell[] row = cells[i];
                Region rowReg = rows.get(i);
                for (int j = 0; j < row.length; j++) {
                    Cell cell = row[j];
                    //初始化每一行
                    cell.putToRow(rowReg);
//                    rowReg.cells.add(cell);
//                    cell.horizontalLine = rowReg;
                    //初始化每一列
                    Region column = columns.get(j);
                    cell.putToColumn(column);
                    //初始化每一个子区域
                    Region pile = findPile(i, j);
                    cell.putToPile(pile);
                }
            }

        }

        private List<Region> createRegionList(final int count) {
            List<Region> list = new ArrayList<>(count);
            for (int i = 0; i < count; i++) {
                list.add(new Region());
            }
            return list;
        }

//        private List<PileLine> createPileList(int PILE_LINE_ROW_COUNT) {
//            throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
//        }

        private void initPiles() {
            //            piles = new ArrayList<>(PILE_LINE_ROW_COUNT * PILE_LINE_COLUMN_COUNT);
            piles = createRegionList(PILE_LINE_ROW_COUNT * PILE_LINE_COLUMN_COUNT);
            //初始化区域横
            horizontalPiles = new ArrayList<>(PILE_LINE_ROW_COUNT);
            //初始化区域纵
            verticalPiles = new ArrayList<>(PILE_LINE_COLUMN_COUNT);
            //将所有pile加载到纵横矩阵中.
            throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
        }

        private Region findPile(int i, int j) {
            throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
        }
    }
}
