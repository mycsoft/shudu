/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package name.myc.sudu;

import java.io.PrintStream;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.SortedSet;
import java.util.TreeSet;

/**
 *
 * @author mayic
 */
public class SudoMain {

    public static void main(String[] args) {
        compute(Arrays.asList(
                //                0, 1, 0, 4, 0, 9, 0, 8, 0,
                //                4, 0, 0, 7, 2, 5, 0, 0, 9,
                //                0, 0, 0, 0, 0, 0, 0, 0, 0,
                //                3, 5, 0, 0, 0, 0, 0, 6, 8,
                //                0, 9, 0, 0, 0, 0, 0, 1, 0,
                //                2, 7, 0, 0, 0, 0, 0, 4, 3,
                //                0, 0, 0, 0, 0, 0, 0, 0, 0,
                //                9, 0, 0, 5, 4, 1, 0, 0, 6,
                //                0, 6, 0, 3, 0, 8, 0, 9, 0
                0, 0, 0, 0, 0, 0, 0, 0, 0,
                1, 0, 0, 0, 4, 0, 2, 8, 3,
                3, 0, 5, 0, 0, 1, 0, 0, 9,
                0, 0, 0, 9, 0, 0, 0, 0, 0,
                2, 6, 0, 0, 0, 0, 0, 1, 8,
                0, 0, 0, 0, 0, 7, 0, 0, 0,
                5, 0, 0, 2, 0, 0, 7, 0, 4,
                7, 4, 6, 0, 8, 0, 0, 0, 5,
                0, 0, 0, 0, 0, 0, 0, 0, 0
        ));
    }

    private static void compute(List<Integer> numbers) {
        //加载原始数据
        Table t = Table.createBySingle(numbers);
        t.printAll();
        System.out.println(String.format("加载成功,需要计算%d个数字.", t.unkonwn));
        //计算
        t.compute();
        System.out.println(String.format("计算结束,还有%d个数字没有计算出来.", t.unkonwn));
        //显示计算结果。
        t.printAll();
        System.out.println("===============================================");
        System.out.println(String.format("计算结束,还有%d个数字没有计算出来.", t.unkonwn));
        if (t.unkonwn == 0) {
            checkResult(t);
        }

    }

    private static void checkResult(Table t) {
        checkResult(t.rows);
        checkResult(t.columns);
        checkResult(t.piles);
        System.out.println("===============================================");
        System.out.println("检查完成,没有错误.");

    }

    private static void checkResult(List<Region> regions) {
        for (Region r : regions) {
            List<Integer> nList = new ArrayList<>(9);
            for (Cell cell : r.cells) {
                if (nList.contains(cell.value)) {
                    throw new RuntimeException(String.format("发现错误,数字%d在[%d,%d]上重复了.", cell.value, cell.row, cell.column));
                } else {
                    nList.add(cell.value);
                }
            }
        }
    }

    /**
     * 单元格.
     *
     * 为防止循环调用,所有组装方法,只从下向上组装.
     */
    static class Cell {

        private static Cell create(int row, int column, int v, final int MAX) {
            SortedSet<Integer> p = new TreeSet<>();
            if (v == 0) {
                for (int i = 0; i < MAX; i++) {
                    p.add(i + 1);
                }
            }
            Cell c = new Cell(row, column, v, p);
            return c;
        }

        int row;
        int column;
        int value;
        /**
         * 备选值.
         */
        SortedSet<Integer> bfts;
        Region pile;
        Region verticalLine;
        Region horizontalLine;

        public Cell(int value) {
            this(0, 0, value);
        }

        public Cell(int row, int column, int value) {
            this(row, column, value, null);
        }

        public Cell(int row, int column, int value, SortedSet<Integer> p) {
            this.row = row;
            this.column = column;
            this.value = value;
            this.bfts = p;
        }

        private void putToRow(Region rowReg) {
            rowReg.cells.add(this);
            horizontalLine = rowReg;
        }

        private void putToColumn(Region column) {
            column.cells.add(this);
            verticalLine = column;

        }

        /**
         * 安装到堆中.
         *
         * @param pile
         */
        private void putToPile(Region pile) {
            pile.cells.add(this);
            this.pile = pile;
//            throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
        }

        private void printResult(PrintStream out) {
            String s;
            if (value > 0) {
                s = String.valueOf(value);
            } else {
                StringBuilder sb = new StringBuilder("(");
                for (Integer n : bfts) {
                    sb.append(n).append(",");
                }
                sb.deleteCharAt(sb.length() - 1).append(")");
                s = sb.toString();
            }
            out.print(s);
        }

    };

    /**
     * 集合，一行，一列，或一个正文区。
     */
    static class Region {

        private static Region create(int count) {
            Region r = new Region();
            r.cells = new ArrayList<>(count);
            r.btfs = new TreeSet<>();
            return r;
        }

        List<Cell> cells;
        SortedSet<Integer> btfs;
        PileLine verticalLine;
        PileLine horizontalLine;

        private void putToRow(PileLine pl) {
            horizontalLine = pl;
            pl.piles.add(this);
        }

        private void putToColumn(PileLine pl) {
            verticalLine = pl;
            pl.piles.add(this);
        }
    }

    /**
     * 堆线.
     *
     * 用于计算三个同行(或同列)方阵之间的数字排除关系. 一般一个堆线为3*1; 用
     */
    static class PileLine {

        private static PileLine create(Table t, int length) {
            PileLine pl = new PileLine();
            pl.parent = t;
            pl.piles = new ArrayList<>(length);
            return pl;
        }

        List<Region> piles;
        Table parent;
    }

    /**
     * 主表格。
     */
    static class Table {

        static final int MAX = 9;
        static final int MAX_ROW = MAX;
        static final int MAX_COLUMN = MAX;
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
                int row = i / MAX_ROW;
                int column = i % MAX_COLUMN;
                //加载时记录行列号.
//                Cell cell = new Cell(row, column, v);
                Cell cell = Cell.create(row, column, v, MAX);
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
            System.out.println("================================================");
            for (int i = 0; i < cells.length; i++) {
                Cell[] row = cells[i];
                System.out.println("------------------------------------------------");
                System.out.print("[" + i + "]");
                for (int j = 0; j < row.length; j++) {
                    Cell c = row[j];
                    System.out.print("\t|");
                    c.printResult(System.out);
                }
                System.out.println("\t|");
            }
            System.out.println("------------------------------------------------");
            System.out.println("================================================");
        }

        /**
         * 简单粗暴的算法。
         */
        private int simpleCompute() {
            //遍历每个cell,记录可能值。直到只有一个可能性。
            boolean changed = false;
            int roll = 0;
            SimpleScaner simpleScaner = new SimpleScaner();
            OnlyOneScaner onlyOneScaner = new OnlyOneScaner();
            GroupScaner groupScaner = new GroupScaner();
            do {
                int c = 0;
                //线性排除法检查
                if (simpleScaner.scan(this)) {
                    c++;
                }
                printAll();
                //唯一法检查;
                if (onlyOneScaner.scan(this)) {
                    c++;
                }
                printAll();
                //分组法检查;
                c += groupScaner.scan(this);
                printAll();

                changed = c > 0;

            } while (unkonwn > 0 && changed);

            return roll;
        }

        /**
         * 初始化计算模型。
         */
        private void initComputeModel() {
            //初始化每一行
//            rows = new ArrayList<>(MAX_ROW);
            rows = createRegionList(MAX_ROW, MAX_COLUMN);
            //初始化每一列
//            columns = new ArrayList<>(MAX_COLUMN);
            columns = createRegionList(MAX_COLUMN, MAX_ROW);

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
                    Region pile = findPileByCell(i, j);
                    cell.putToPile(pile);
                }
            }

        }

        private List<Region> createRegionList(final int count, final int subCount) {
            List<Region> list = new ArrayList<>(count);
            for (int i = 0; i < count; i++) {
                list.add(Region.create(subCount));
            }
            return list;
        }

        private List<PileLine> createPileList(int count, int subCount) {
            List<PileLine> list = new ArrayList<>(count);
            for (int i = 0; i < count; i++) {
                PileLine pl = PileLine.create(this, subCount);
                list.add(pl);
            }
            return list;
        }

        private void initPiles() {
            //            piles = new ArrayList<>(PILE_LINE_ROW_COUNT * PILE_LINE_COLUMN_COUNT);
            piles = createRegionList(PILE_LINE_ROW_COUNT * PILE_LINE_COLUMN_COUNT,
                    PILE_ROW_COUNT * PILE_COLUMN_COUNT);
            //初始化区域横
            horizontalPiles = createPileList(PILE_LINE_ROW_COUNT, PILE_COLUMN_COUNT);
            //初始化区域纵
            verticalPiles = createPileList(PILE_LINE_COLUMN_COUNT, PILE_ROW_COUNT);
            //将所有pile加载到纵横矩阵中.
            for (int i = 0; i < piles.size(); i++) {
                Region pile = piles.get(i);
                int row = i / PILE_COLUMN_COUNT;
                int column = i % PILE_COLUMN_COUNT;
                pile.putToRow(horizontalPiles.get(row));
                pile.putToColumn(verticalPiles.get(column));

            }
        }

        /**
         * 根据单元格的位置查询对应的Pile.
         *
         * @param row
         * @param column
         * @return
         */
        private Region findPileByCell(int row, int column) {
            int index = row / PILE_ROW_COUNT * PILE_COLUMN_COUNT + column / PILE_COLUMN_COUNT;
            if (index >= piles.size()) {
                throw new IndexOutOfBoundsException(
                        String.format("数组越界: %d >= %d.(row:%d,column:%d)", index, piles.size(), row, column));
            }
            return piles.get(index);
        }

//        private boolean scanBft(Cell cell) {
//            SimpleScaner scaner = new SimpleScaner();
//            scaner.scanBft(cell);
//            return scaner.onlyOne;
//        }
    }

    static interface Scaner {

        boolean scan(Table t);

//        /**
//         * @return the onlyOne
//         */
//        boolean isOnlyOne();
    }

    static interface Scaner2 {

        int scan(Table t);

//        /**
//         * @return the onlyOne
//         */
//        boolean isOnlyOne();
    }

    static abstract class AbstractScaner implements Scaner {

        protected boolean changed = false;

        /**
         * @return the onlyOne
         */
//        @Override
    }

    /**
     * 简单检查器.
     *
     * 只进行简单的排除法.
     */
    static class SimpleScaner extends AbstractScaner {

        protected boolean onlyOne = false;

        public boolean isOnlyOne() {
            return onlyOne;
        }

        @Override
        public boolean scan(Table t) {
            //本次是否有变化.
            boolean thisChanged = false;
            //上次检查是否有变化?
            boolean lastChanged = false;
            int roll = 0;
            do {
                //单次是否有变化.
                lastChanged = false;
                for (Cell[] row : t.cells) {
                    for (Cell cell : row) {
//                        onlyOne = false;
                        boolean changedNow = scanBft(cell);
                        if (changedNow) {
                            lastChanged = true;
                            thisChanged = true;
                        }
                        boolean needSaveValue = isOnlyOne() && cell.value <= 0;
                        if (needSaveValue) {
                            cell.value = cell.bfts.first();
                            cell.bfts.clear();
                            t.unkonwn--;
                        }
                    }
                }
                roll++;
            } while (lastChanged);

            return thisChanged;
        }

        /**
         * 扫描备选值.
         *
         * @param cell
         * @return 是否有变化.
         */
        protected boolean scanBft(Cell cell) {
            SortedSet<Integer> bfts = cell.bfts;
            //是否是唯一值.
            onlyOne = bfts.size() < 2;
            if (isOnlyOne()) {
                //已经是唯一值,不需要检查了.
                return false;
            }
            changed = false;
            //遍历行
            scanBft(bfts, cell.horizontalLine.cells);
            //遍历列
            scanBft(bfts, cell.verticalLine.cells);
            //遍历堆
            scanBft(bfts, cell.pile.cells);
            return changed;
        }

        protected void scanBft(SortedSet<Integer> bfts, List<Cell> cells) {
            if (!isOnlyOne()) {
                for (Cell rc : cells) {
                    if (bfts.contains(rc.value)) {
                        changed = true;
                        bfts.remove(Integer.valueOf(rc.value));
                        if (bfts.size() < 2) {
                            onlyOne = true;
                            return;
                        }
                    }
                }
            }
        }

    }

    /**
     * 唯一值检查器.
     *
     * 只进行唯一的值计算.
     */
    static class OnlyOneScaner extends AbstractScaner {

        @Override
        public boolean scan(Table t) {
            //本次是否有变化.
            boolean thisChanged = false;
            //上次检查是否有变化?
//            boolean lastChanged = false;
//            int roll = 0;
//            do {
            //单次是否有变化.
//                lastChanged = false;
            int c = 0;
            //按行
            c += scanRegions(t.rows);
            if (c > 0) {
                //如果有变化,那么需要再进行次全面扫描.
                t.unkonwn -= c;
                return true;
            }
            //按列
            c += scanRegions(t.columns);
            if (c > 0) {
                //如果有变化,那么需要再进行次全面扫描.
                t.unkonwn -= c;
                return true;
            }
            //按堆
            c += scanRegions(t.piles);
            if (c > 0) {
                //如果有变化,那么需要再进行次全面扫描.
                t.unkonwn -= c;
                return true;
            }
//                lastChanged = c > 0;
//                roll++;
//            } while (lastChanged);

            return thisChanged;
        }

        private int scanRegions(List<Region> regions) {
            int c = 0;
            for (Region r : regions) {
                c += scanRegion(r.cells);
                System.out.println("==============================================");
            }
            return c;
        }

        protected int scanRegion(List<Cell> cells) {
//            boolean findOnlyOne = false;
            int foundCount = 0;
            Map<Integer, List<Cell>> bftsMap = new HashMap<>(9);
            //统计一下所有备选值出现的次数.
            for (Cell cell : cells) {
                if (cell.value <= 0) {
                    for (Integer bft : cell.bfts) {
                        List<Cell> cs = bftsMap.get(bft);
                        if (cs == null) {
                            cs = new ArrayList<>();
                            bftsMap.put(bft, cs);
                        }
                        cs.add(cell);
                    }
                }
            }

            //找出唯一值进行修改.
            for (Map.Entry<Integer, List<Cell>> entry : bftsMap.entrySet()) {
                if (entry.getValue().size() == 1) {
                    foundCount++;
                    //保存维一值.
                    Cell c = entry.getValue().get(0);
                    c.value = entry.getKey();
                    c.bfts.clear();
                    System.out.println(String.format("%d | 找到唯一值 %d:[%d:%d:{%s}]", foundCount, c.value, c.row, c.column, c.bfts.toString()));
//                    findOnlyOne = true;
//                    onlyOne = true;
                }
            }
            return foundCount;
        }
    }

    /**
     * 分组扫描器。
     *
     * 通过取同类项分组的方式，缩小备选值范围。
     *
     * 算法思路：
     *
     * 1. 针对一个堆中的所有未填入值进行分析，
     *
     * 2. 依次排除其中每个值（n）存在的单元格集合（listCn），等到余下的单元格集合(listUCn)。
     *
     * 3. 如果listUCn的备选值数量与单元格数相等(listUCn.bfts.size =
     * listUCn.cells.size),那么就可以确认listUCn中的备选值，不会出现在listCn中。
     *
     * 4. 则从listCn中删除listUCn.bfts.
     *
     * 5. 以listUCn为总集合，再进行一次遍历。直到bfts或cells为空。
     *
     */
    static class GroupScaner implements Scaner2 {

        @Override
        public int scan(Table t) {
            int c = 0;
            //遍历所有的堆。
            c += scan(t.rows);
            c += scan(t.columns);
            c += scan(t.piles);
            return c;
        }

        private int scan(List<Region> regions) {
            int c = 0;
            for (Region r : regions) {
                c += scan(r);
            }
            return c;
        }

        private int scan(Region region) {
            return scanCells(region.cells);
        }

        private SortedSet<Integer> findAllBtfs(Collection<Cell> cells) {
            SortedSet<Integer> allBtfs = new TreeSet<>();
            cells.forEach((cell) -> {
                cell.bfts.forEach((bft) -> {
                    allBtfs.add(bft);
                });
            });
            return allBtfs;
        }

        private int scanCells(List<Cell> cells) {
            //找出所有的备选值。
            SortedSet<Integer> allBtfs = findAllBtfs(cells);
            //依次排除其中每个值（n）存在的单元格集合（listCn），等到余下的单元格集合(listUCn)。
            return groupByNumber(allBtfs, cells, cells, cells);
            //如果listUCn的备选值数量与单元格数相等(listUCn.bfts.size = listUCn.cells.size),那么就可以确认listUCn中的备选值，不会出现在listCn中。
            //则从listCn中删除listUCn.bfts.
            //以listUCn为总集合，再进行一次遍历。直到bfts或cells为空。
        }

        private int groupByNumber(Collection<Integer> numbers, List<Cell> all, List<Cell> has, List<Cell> notHas) {
            int changed = 0;
            //依次排除其中每个值（n）存在的单元格集合（listCn），等到余下的单元格集合(listUCn)。
            for (Integer n : numbers) {

                Region listCn = Region.create(all.size());
                Region listUCn = Region.create(all.size());
                int allCount = 0;
                for (Cell cell : all) {
                    if (cell.value > 0) {
                        continue;
                    }
                    allCount++;
                    if (cell.bfts.contains(n)) {
                        listCn.cells.add(cell);
                        listCn.btfs.addAll(cell.bfts);
                    } else {
                        listUCn.cells.add(cell);
                        listUCn.btfs.addAll(cell.bfts);
                    }
                }

                /* 如果listUCn的备选值数量与单元格数相等(listUCn.bfts.size = 
                listUCn.cells.size),那么就可以确认listUCn中的备选值，不会出现在listCn中。
                 */
                boolean ucnIsGroup = listUCn.cells.size() == listUCn.btfs.size()
                        && listUCn.cells.size() < allCount;
                if (ucnIsGroup) {
                    //则从listCn中删除listUCn.bfts.
                    listCn.btfs.removeAll(listUCn.btfs);
                    changed = listCn.cells.stream().map((c) -> {
                        //从每个单元格中删除。
                        int before = c.bfts.size();
                        c.bfts.removeAll(listUCn.btfs);
                        int bftsCount = c.bfts.size();
                        boolean changeCell = before - bftsCount > 0;
                        if (changeCell) {
                            if (bftsCount == 1) {
                                c.value = c.bfts.first();
                                c.bfts.clear();
                                c.horizontalLine.horizontalLine.parent.unkonwn --;
                            }
                            return 1;
                        }
                        return 0;
                    }).reduce(changed, Integer::sum);

//                    changed = listCn.cells.stream().map((c) -> {
//                        int before = c.bfts.size();
//                        c.bfts.removeAll(listUCn.btfs);
//                        return before - c.bfts.size() > 0 ? 1 : 0;
//                    }).reduce(changed, Integer::sum);
                }
                //以listUCn为总集合，再进行一次遍历。直到bfts或cells为空。
                if (numbers.size() > 1) {
                    Collection<Integer> subNumbers = new ArrayList<>(numbers);
                    subNumbers.remove(n);
                    changed += groupByNumber(subNumbers, listUCn.cells, has, notHas);
                }
            }
            return changed;
        }

    }
}
