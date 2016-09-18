/**
 * Created by kevin on 2016/7/3.
 */
/**
 * Created by kevin on 2016/6/29.
 */
import javax.swing.*;
import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.event.*;
import java.awt.geom.GeneralPath;
import java.util.List;
import java.util.Random;
import java.util.*;
import java.awt.geom.*;
import java.io.*;
import java.awt.image.*;
class Diagram_Panel extends JPanel {
    //参数：
    // n: 4~6
    private static int N1 = 6;
    private static int N2 = 8;
    // e: 3~5
    private static int E1 = 3;
    private static int E2 = 5;
    // d0
    private static int D0 = 150;
    // c
    private static int C = 6;
    // 节点个数
    private static int NUMBEROFVERTICES = 100;
    // 普通点速度
    private static int S1 = 2;
    //簇头速度
    private static int S2 = 1;
    //移动速度
    private static int TIMETOMOVE = 20;
    private static int ratio = 100;

    private HashSet<Integer> verticesInCluster;
    private HashSet<Integer> verticesConnectedToi;
    private ArrayList<ArrayList<Integer>> resultOfCluster;
    private Rectangle area = new Rectangle(50,50,1000,1000);
    private Rectangle area2 = new Rectangle(1100,50,200,1000);
    private ArrayList<Integer> clusterHeaders;
    private Rectangle btnNew = new Rectangle(10,10,82,22);
    private Rectangle btnMove = new Rectangle(100,10,82,22);
    private Rectangle btnUseless = new Rectangle(190,10,82,22);
    private Pnt point1 = new Pnt(0,50,50);
    private Pnt point2 = new Pnt(0,1050,50);
    private Pnt point3 = new Pnt(0,1050,1050);
    private Pnt point4 = new Pnt(0,50,1050);

    private int state_new_button;
    private int state_move_button;
    private int state_useless_button;
    private Random rnd = new Random();
    private int[][] vertices;
    private int[][] params;
    private int[][] linked;
    private int[][] starLinked;
    private int[] argument_n;
    private int[] argument_e;
    private ArrayList<Triangle> trueNodelist;

    private int[][] linkedHeaders;
    private ArrayList<HashSet<Integer>> pointOrClusterNotInNetwork = new ArrayList<HashSet<Integer>>();
    private HashMap<Integer,java.util.List<Map.Entry<Double,Triangle>>> headerMap = new HashMap<Integer,java.util.List<Map.Entry<Double,Triangle>>>();
    private HashMap<Integer,java.util.List<Pnt>> headerAndPoints = new HashMap<Integer,java.util.List<Pnt>>();
    private HashMap<Integer,java.util.List<Pnt>> headerAndNormalPoints = new HashMap<Integer,java.util.List<Pnt>>();
    private ArrayList<Integer> pointNotInNetwork = new ArrayList<Integer>();
    private int countOfImage = 0;
//    private Hash

    public Diagram_Panel (){
        JButton jb = new JButton("Generate png");
        jb.addActionListener(new ActionListener(){
            public void actionPerformed(ActionEvent e){
                countOfImage++;
                BufferedImage bi = new BufferedImage(1090,1090,BufferedImage.TYPE_INT_ARGB);
                Graphics2D g2d = bi.createGraphics();
                g2d.setColor(Color.WHITE);
                g2d.fillRect(0,0,1090,1090);
                //g2d.dispose();
                paint(g2d);
                try {
                ImageIO.write(bi,"PNG",new File("Diagrams"+ countOfImage + ".png"));
                } catch(IOException e3) {
                    e3.printStackTrace();
                }
            }
        });
        add(jb);
        //setBackground(Color.WHITE);	
        addMouseMotionListener( new MouseMotionAdapter() {
            @Override 
            public void mouseMoved (MouseEvent me){
                if(me.getX() >= btnNew.x && me.getX() <= btnNew.x + btnNew.width && me.getY() >= btnNew.y && me.getY() <= btnNew.y + btnNew.height){
                    if(state_new_button != 1){
                        state_new_button = 1;
                        repaint();
                    }	
                }else{
                    if(state_new_button != 0)	{
                        state_new_button = 0;
                        repaint();
                    }
                }
                //for btnMove
                if(me.getX() >= btnMove.x && me.getX() <= btnMove.x + btnMove.width && me.getY() >= btnMove.y && me.getY() <= btnMove.y + btnMove.height){
                    if(state_move_button != 1){
                        state_move_button = 1;
                        repaint();
                    }	
                }else{
                    if(state_move_button != 0)	{
                        state_move_button = 0;
                        repaint();
                    }
                }	
            }
            @Override 
            public void mouseDragged(MouseEvent me){
                if(state_new_button == 2 || state_new_button == 1){
                    if(me.getX() >= btnNew.x && me.getX() <= btnNew.x + btnNew.width && me.getY() >= btnNew.y && me.getY() <= btnNew.y + btnNew.height){  if(state_new_button != 2) state_new_button = 2;  }	
                    else if(state_new_button != 1) state_new_button = 1;
                    repaint();
                }
                //for btnMove
                if(state_move_button == 2 || state_move_button == 1){
                    if(me.getX() >= btnMove.x && me.getX() <= btnMove.x + btnMove.width && me.getY() >= btnMove.y && me.getY() <= btnMove.y + btnMove.height){  if(state_move_button != 2) state_move_button = 2;  }	
                    else if(state_move_button != 1) state_move_button = 1;
                    repaint();
                }
            }
        });
        addMouseListener(new MouseAdapter(){
            @Override 
            public void	mousePressed (MouseEvent me) {
                
                if(state_new_button == 1)	{
                    state_new_button = 2;
                    
                    newGraph();
                }
                if(state_move_button == 1)	{
                    state_move_button = 2;
                    //newGraph();
                    moveAllPoints();
                    //printrec();
                }
            }

            @Override
            public void mouseReleased(MouseEvent me) {
                if(me.getX() >= btnNew.x && me.getX() <= btnNew.x + btnNew.width && me.getY() >= btnNew.y && me.getY() <= btnNew.y + btnNew.height)	
                    state_new_button = 1;
                else
                    state_new_button = 0;
                repaint();
                //for btnMove
                if(me.getX() >= btnMove.x && me.getX() <= btnMove.x + btnMove.width && me.getY() >= btnMove.y && me.getY() <= btnMove.y + btnMove.height)	
                    state_move_button = 1;
                else
                    state_move_button = 0;
                repaint();
            }
        });
    }
    private void mouseOver (Rectangle btn, String str, Graphics g){
        Color tmp = g.getColor();
        g.setColor(Color.GRAY);
        g.fillRect(btn.x, btn.y, btn.width + 1, btn.height + 1);
        g.setColor(Color.WHITE);
        g.drawLine(btn.x, btn.y, btn.x, btn.y + btn.height - 1);
        g.drawLine(btn.x, btn.y, btn.x + btn.width - 1, btn.y);
        g.drawString(str, btn.x + 10, btn.y + 15);
        g.setColor(tmp);
    }
    private void mouseOff(Rectangle btn, String str, Graphics g){
        Color tmp = g.getColor();
        g.setColor(Color.BLACK);
        g.drawRect(btn.x + 1, btn.y + 1, btn.width, btn.height);
        g.setColor(Color.WHITE);
        g.fillRect(btn.x, btn.y, btn.width, btn.height);
        g.setColor(Color.BLACK);
        g.drawRect(btn.x, btn.y, btn.width, btn.height);
        g.drawString(str,btn.x + 10, btn.y + 15);
        g.setColor(tmp);
    }
    private void mouseDown (Rectangle btn, String str, Graphics g){
        Color tmp = g.getColor();
        g.setColor(Color.DARK_GRAY);
        g.fillRect(btn.x + 1, btn.y + 1, btn.width + 1, btn.height + 1);
        g.setColor(Color.WHITE);
        g.drawLine(btn.x + 2, btn.y + btn.height + 1, btn.x + btn.width + 1, btn.y + btn.height + 1);
        g.drawLine(btn.x + btn.width + 1, btn.y + 2, btn.x + btn.width + 1, btn.y + btn.height + 1);
        g.drawString(str,btn.x + 11, btn.y + 16);
        g.setColor(tmp);
    }
    @Override
    public void paint(Graphics g) {
        super.paint(g);	
        System.out.println("paint");
        switch(state_new_button) {
            case 0: 
                mouseOff(btnNew, "New Graph", g);
                break;
            case 1:
                mouseOver(btnNew, "New Graph", g);
                break;
            case 2:
                mouseDown(btnNew, "New Graph", g);
                break;
        }
        switch(state_move_button) {
            case 0: 
                mouseOff(btnMove, "Move Graph", g);
                break;
            case 1: 
                mouseOver(btnMove, "Move Graph", g);
                break;
            case 2: 
                mouseDown(btnMove, "Move Graph", g);
                break;
        }
        //g.SizeToDisplay = 1;
        Graphics2D g2 = (Graphics2D) g;
        /*
        for(int i = 0; i < vertices.length;i++)
            for(int j = 0;j < vertices.length;j++)
                if(linked[i][j] == 1) {
                    //drawAL(vertices[i][0] + 2, vertices[i][1] + 2, vertices[j][0] + 2, vertices[j][1] + 2,g2);
                    g.drawLine(vertices[i][0] + 2, vertices[i][1] + 2, vertices[j][0] + 2, vertices[j][1] + 2);
                    //g.drawString(linked[i][j] + "",(vertices[i][0] + vertices[j][0])/2, (vertices[i][1] + vertices[j][1])/2);
                }

        */
        //drawTotalTriangle(g);
        drawThiessenPolygons(g);
        //for(int i = 0;i < vertices.length;i++){
        //    boolean isMainVertice = true;
        //    for(int j = 0;j < vertices.length;j++) {
        //        if(linked[i][j] == 1)  isMainVertice = false;
        //    }
        //    if(isMainVertice) {
        //        g.setColor(Color.BLACK);
        //        g.fillOval(vertices[i][0], vertices[i][1],7,7);
        //        g.drawOval(vertices[i][0], vertices[i][1],7,7);
        //    }
        //    else{
        //        g.setColor(Color.GRAY);
        //        g.fillOval(vertices[i][0], vertices[i][1],5,5);
        //        g.drawOval(vertices[i][0], vertices[i][1],5,5);
        //    }
        //}
        drawStar(g);
        for(int i = 0;i < vertices.length;i++){
            for(int j = 0;j < vertices.length;j++){
                if(linked[i][j] == 1) {
                    linked[j][i] = 1; 
                }
            } 
        }
        drawTriangle(g);
        for(int i = 0;i < vertices.length;i++){
            if(clusterHeaders.contains(i)) {
                g.setColor(Color.BLACK);
                g.fillOval(vertices[i][0], vertices[i][1],7,7);
                g.drawOval(vertices[i][0], vertices[i][1],7,7);
                g.drawString(i  + "", vertices[i][0] - 2, vertices[i][1] - 2);
            }
            else{
                g.setColor(Color.GRAY);
                g.fillOval(vertices[i][0], vertices[i][1],3,3);
                g.drawOval(vertices[i][0], vertices[i][1],3,3);
                g.drawString(i  + "", vertices[i][0] - 2, vertices[i][1] - 2);
            }
        }
        g.setColor(Color.BLACK);
        g.drawRect(area.x, area.y, area.width, area.height);
        g.drawRect(area2.x, area2.y, area2.width, area2.height);
        //双向链接
    }
    public void dealWithMovedStar(){
    
        java.util.List<Map.Entry<Integer,java.util.List<Pnt>>>  entryHeaderAndNormalPoints= new ArrayList<Map.Entry<Integer,java.util.List<Pnt>>>(headerAndNormalPoints.entrySet());
        for(Map.Entry<Integer,java.util.List<Pnt>> tmpEntry: entryHeaderAndNormalPoints){
            java.util.List<Pnt> stars = tmpEntry.getValue();
            int index = tmpEntry.getKey();
            System.out.println("debug0001 " + index);
            for(Pnt i : stars){
                System.out.println("stars : " + i.getIndex());
            }
            ArrayList<Integer> tmpPntNotInNet = new ArrayList<Integer>();
            for(int i : pointNotInNetwork)
            {
                java.util.List<Pnt> polygon = headerAndPoints.get(index);
                if(isInPolygon(new Pnt(i,vertices[i][0],vertices[i][1]),polygon)){
                    tmpPntNotInNet.add(i);
                }
            }
            
            for(int i : tmpPntNotInNet){
                System.out.print("debug0002 " + i);
            }
                System.out.println();
            int flag = 0;
            for(int i = 0;i < vertices.length;i++){
                if(linkedHeaders[index][i] == 1) flag++;
            }
            int flag1 = 0;
            for(int i = 0;i < vertices.length;i++){
                if(starLinked[index][i] == 1) flag1++;
            }
            System.out.print("debug0003 " + flag);
            System.out.print("debug0004 " + flag1);
            int flagOfAll = flag + flag1;
            System.out.print("debug0005 " + flagOfAll);
            int currentN = argument_n[index];
            int numberOfDirectoryLinked = stars.size();
            System.out.println("index=" + index);
            for(int i = 0;i < tmpPntNotInNet.size();i++){
                if(currentN > flagOfAll){
                    starLinked[index][tmpPntNotInNet.get(i)] = 1;
                    starLinked[tmpPntNotInNet.get(i)][index] = 1;
                    for(Pnt pnt : headerAndNormalPoints.get(index)){
                        System.out.print( " " + pnt.getIndex());
                    }
                    System.out.println();
                    stars.add(new Pnt(tmpPntNotInNet.get(i),vertices[tmpPntNotInNet.get(i)][0],vertices[tmpPntNotInNet.get(i)][1]));
                    System.out.println(index + " add" + tmpPntNotInNet.get(i));
                    //System.out.print("debug0006 " + flagOfAll);
                    for(Pnt pnt : headerAndNormalPoints.get(index)){
                        System.out.print( " " + pnt.getIndex());
                    }
                    System.out.println();
                    //headerAndNormalPoints.get(index).add(new Pnt(tmpPntNotInNet.get(i),vertices[tmpPntNotInNet.get(i)][0],vertices[tmpPntNotInNet.get(i)][1]));
                    for(Pnt pnt : headerAndNormalPoints.get(index)){
                        System.out.print( " " + pnt.getIndex());
                    }
                    System.out.println();
                    System.out.println("1. " + index + " -> " + tmpPntNotInNet.get(i));
                    flagOfAll++;
                    numberOfDirectoryLinked++;
                    /*
                }else if(argument_n[index] == flagOfAll && flag > 2){
                    starLinked[index][tmpPntNotInNet.get(i)] = 1;
                    starLinked[tmpPntNotInNet.get(i)][index] = 1;
                    stars.add(new Pnt(tmpPntNotInNet.get(i),vertices[tmpPntNotInNet.get(i)][0],vertices[tmpPntNotInNet.get(i)][1]));
                    System.out.println(index + " add" + "tmpPntNotInNet.get(i)");
                    headerAndNormalPoints.get(index).add(new Pnt(tmpPntNotInNet.get(i),vertices[tmpPntNotInNet.get(i)][0],vertices[tmpPntNotInNet.get(i)][1]));
                    System.out.println("2. " + index + " -> " + tmpPntNotInNet.get(i));
                    numberOfDirectoryLinked++;

                    TreeMap<Double,Integer> temp = new TreeMap<Double,Integer>();
                    int[] clusterWithOneHeader = new int[vertices.length];
                    for(int j = 0; j < vertices.length;j++){
                        if(linkedHeaders[index][j] == 1){
                            temp.put(weightBetweenTwoHeaders(index,j),j);
                        }
                    } 
                        java.util.List<Map.Entry<Double,Integer>> list = new ArrayList<Map.Entry<Double,Integer>>(temp.entrySet());
                            linkedHeaders[index][list.get(0).getValue()] = 0;
                            linkedHeaders[list.get(0).getValue()][index] = 0;
                    
                    flag--;*/
                }else if(argument_n[index] == flagOfAll){ 
                    //int ind = (int)(numberOfDirectoryLinked * Math.random());
                    TreeMap<Double,Integer> sortOfCluster = new TreeMap<Double,Integer>(); 
                    for(int l = 0;l < stars.size();l++){
                    sortOfCluster.put(lengthOfLine(i,stars.get(l).getIndex(),vertices),stars.get(l).getIndex());
                    }
                java.util.List<Map.Entry<Double,Integer>> list = new ArrayList<Map.Entry<Double,Integer>>(sortOfCluster.entrySet());
                //Collections.reverse(list);
                    //starLinked[.get(ind)][tmpPntNotInNet.get(i)] = 1;
                    //starLinked[tmpPntNotInNet.get(i)][tmpPntNotInNet.get(ind)] = 1;
                    starLinked[list.get(0).getValue()][tmpPntNotInNet.get(i)] = 1;
                    starLinked[tmpPntNotInNet.get(i)][list.get(0).getValue()] = 1;
                    stars.add(new Pnt(tmpPntNotInNet.get(i),vertices[tmpPntNotInNet.get(i)][0],vertices[tmpPntNotInNet.get(i)][1]));
                    System.out.println(index + " add" + tmpPntNotInNet.get(i));
                    //System.out.println(index + " add" + "tmpPntNotInNet.get(i)");
                    for(Pnt pnt : headerAndNormalPoints.get(index)){
                        System.out.print( " " + pnt.getIndex());
                    }
                    System.out.println();
                    //headerAndNormalPoints.get(index).add(new Pnt(tmpPntNotInNet.get(i),vertices[tmpPntNotInNet.get(i)][0],vertices[tmpPntNotInNet.get(i)][1]));
                    for(Pnt pnt : headerAndNormalPoints.get(index)){
                        System.out.print( " " + pnt.getIndex());
                    }
                    System.out.println();
                    //System.out.println("3. " + stars.get(ind).getIndex() + " -> " + tmpPntNotInNet.get(i));
                }
            }
        }
    }
    public void dealWithStar(){
    
        java.util.List<Map.Entry<Integer,java.util.List<Pnt>>>  entryHeaderAndNormalPoints= new ArrayList<Map.Entry<Integer,java.util.List<Pnt>>>(headerAndNormalPoints.entrySet());
        for(Map.Entry<Integer,java.util.List<Pnt>> tmpEntry: entryHeaderAndNormalPoints){
            java.util.List<Pnt> stars = tmpEntry.getValue();
            int index = tmpEntry.getKey();
            int flag = 0;
            for(int i = 0;i < vertices.length;i++){
                if(linkedHeaders[index][i] == 1) flag++;
            }
            int flagOfAll = flag;
            int flagOfAllNormal = 0;
            boolean isReachN = false;
            int currentN = argument_n[index];
            int numberOfDirectoryLinked = 0;
            System.out.println("index=" + index + " stars.size()" + stars.size());
            for(int i = 0;i < stars.size();i++){
                if(currentN > flagOfAll){
                    starLinked[index][stars.get(i).getIndex()] = 1;
                    starLinked[stars.get(i).getIndex()][index] = 1;
                    System.out.println("1. " + index + " -> " + stars.get(i).getIndex());
                    flagOfAll++;
                    flagOfAllNormal++;
                    numberOfDirectoryLinked++;
                }else if(argument_n[index] == flagOfAll && flag > 2){
                    starLinked[index][stars.get(i).getIndex()] = 1;
                    starLinked[stars.get(i).getIndex()][index] = 1;
                    flagOfAllNormal++;
                    System.out.println("2. " + index + " -> " + stars.get(i).getIndex());
                    numberOfDirectoryLinked++;

                    TreeMap<Double,Integer> temp = new TreeMap<Double,Integer>();
                    int[] clusterWithOneHeader = new int[vertices.length];
                    for(int j = 0; j < vertices.length;j++){
                        if(linkedHeaders[index][j] == 1){
                            temp.put(weightBetweenTwoHeaders(index,j),j);
                        }
                    } 
                        java.util.List<Map.Entry<Double,Integer>> list = new ArrayList<Map.Entry<Double,Integer>>(temp.entrySet());
                        for(Map.Entry<Double,Integer> item : list){
                            System.out.println("item.getValue(): " + item.getValue() + "index: " + index);   
                            linkedHeaders[index][item.getValue()] = 0;
                            linkedHeaders[item.getValue()][index] = 0;

                            int numberOfTheSecondHeader = 0;
                            for(int q = 0;q < vertices.length;q++){
                                if(linkedHeaders[item.getValue()][q] == 1) numberOfTheSecondHeader++;
                            }
                        System.out.println("flag0: " +flag);
                            if(numberOfTheSecondHeader < 2){
                                isReachN = true;
                                linkedHeaders[index][item.getValue()] = 1;
                                linkedHeaders[item.getValue()][index] = 1;
                            }else {
                                flag--;
                                break;
                            }
                        }
                        System.out.println("flag: " +flag);
                        /*
                            linkedHeaders[index][list.get(0).getValue()] = 0;
                            linkedHeaders[list.get(0).getValue()][index] = 0;
                            int numberOfTheSecondHeader = 0;
                            for(int q = 0;q < vertices.length;q++){
                                if(linkedHeaders[list.get(0).getValue()][q] == 1) numberOfTheSecondHeader++;
                            }
                            if(numberOfTheSecondHeader < 2){
                                isReachN = true;
                                linkedHeaders[index][list.get(0).getValue()] = 1;
                                linkedHeaders[list.get(0).getValue()][index] = 1;
                            }else {
                                flag--;
                            }
                   */ 
                }else if(flag == 2 || isReachN){ 
                    TreeMap<Double,Integer> sortOfCluster = new TreeMap<Double,Integer>(); 
                    System.out.println("flagOfAllNormal" + flagOfAllNormal);
                    for(int l = 0;l < flagOfAllNormal;l++){
                        if(l != i){
                            sortOfCluster.put(lengthOfLine(stars.get(i).getIndex(),stars.get(l).getIndex(),vertices),stars.get(l).getIndex());
                        }
                    }
                    java.util.List<Map.Entry<Double,Integer>> list = new ArrayList<Map.Entry<Double,Integer>>(sortOfCluster.entrySet());
                    //Collections.reverse(list);
                    //int ind = (int)(numberOfDirectoryLinked * Math.random());
                    starLinked[list.get(0).getValue()][stars.get(i).getIndex()] = 1;
                    starLinked[stars.get(i).getIndex()][list.get(0).getValue()] = 1;
                    flagOfAllNormal++;
                    //starLinked[stars.get(ind).getIndex()][stars.get(i).getIndex()] = 1;
                    //starLinked[stars.get(i).getIndex()][stars.get(ind).getIndex()] = 1;
                    System.out.println("3. " + stars.get(i).getIndex() + " -> " + list.get(0).getValue());
                }
            }
        }
    }
    public void drawStar(Graphics g){
        Graphics2D g2 = (Graphics2D) g;
        BasicStroke stroke =new BasicStroke(2);
        g2.setStroke(stroke);
        g.setColor(Color.red);

        for(int i = 0; i < vertices.length;i++)
            for(int j = 0;j < vertices.length;j++)
                if(starLinked[i][j] == 1) {
                    //drawAL(vertices[i][0] + 2, vertices[i][1] + 2, vertices[j][0] + 2, vertices[j][1] + 2,g2);
                    g.drawLine(vertices[i][0] + 2, vertices[i][1] + 2, vertices[j][0] + 2, vertices[j][1] + 2);
                    //g.drawString(linked[i][j] + "",(vertices[i][0] + vertices[j][0])/2, (vertices[i][1] + vertices[j][1])/2);
                }
    }
    public void drawStarBak(Graphics g){
        Graphics2D g2 = (Graphics2D) g;
        BasicStroke stroke =new BasicStroke(3);
        g2.setStroke(stroke);
        g.setColor(Color.orange);

        java.util.List<Map.Entry<Integer,java.util.List<Pnt>>>  entryHeaderAndPoints= new ArrayList<Map.Entry<Integer,java.util.List<Pnt>>>(headerAndPoints.entrySet());
        for(Map.Entry<Integer,java.util.List<Pnt>> tmpEntry: entryHeaderAndPoints)
        {
        java.util.List<Pnt> polygon = tmpEntry.getValue();
        int index = tmpEntry.getKey();
        System.out.println("index  " + index);
        System.out.println("polygon.size()  " + polygon.size());
        for(Pnt pnt : polygon){
            System.out.printf("%3d",pnt.getIndex());
        }
        System.out.println();
        System.out.println();
           for(int i = 0;i < vertices.length;i++){
                if(!clusterHeaders.contains(i) && isInPolygon(new Pnt(i,vertices[i][0],vertices[i][1]),polygon)) {
                    g.drawLine(vertices[index][0] + 2, vertices[index][1] + 2, vertices[i][0] + 2, vertices[i][1] + 2);
                }
           
           }  
        
        }

    }
    public boolean isInPolygon(Pnt normalPoint,java.util.List<Pnt> polygon){
        GeneralPath p = new GeneralPath();
        Point2D.Double np = new Point2D.Double(normalPoint.getCoordinates()[0],normalPoint.getCoordinates()[1]);
        System.out.println("debug100：" + polygon.size());
        Point2D.Double first = new Point2D.Double(polygon.get(0).getCoordinates()[0],polygon.get(0).getCoordinates()[1]);
        p.moveTo(first.x,first.y);

        for(Pnt pnt : polygon){
            p.lineTo(pnt.getCoordinates()[0],pnt.getCoordinates()[1]); 
        }
        p.lineTo(first.x,first.y);
        p.closePath();
        
        return p.contains(np);
        //return false;
    }
    public void dealWithMatrix(){
            int numberOfHeaders = 0;
            Map<Integer,Integer> big2small = new TreeMap<Integer,Integer>();
            for(int i = 0; i < vertices.length;i++) {
                for(int j = 0; j < vertices.length;j++) {
                    if(linkedHeaders[i][j] == 1){
                        big2small.put(numberOfHeaders,i);
                        numberOfHeaders++;
                        break;
                    }
                }
            }
            double[][] Matrix = new double[numberOfHeaders][numberOfHeaders];
            for(int i = 0; i < numberOfHeaders;i++) {
                for(int j = 0; j < numberOfHeaders;j++) {
                    if(linkedHeaders[big2small.get(i)][big2small.get(j)] == 1){
                        Matrix[i][j] = lengthOfLine(big2small.get(i),big2small.get(j),vertices);
                    }else {
                        Matrix[i][j] = -1;
                    }
                }
            }
            Connectivity connectivity = new Connectivity(NUMBEROFVERTICES);
            connectivity.getEigenvalue(Matrix,Matrix.length);
    }
    public void dealWithThiessenPolygons(){
            java.util.List<Map.Entry<Integer,java.util.List<Map.Entry<Double,Triangle>>>> listHeaderForMap = new ArrayList<Map.Entry<Integer,java.util.List<Map.Entry<Double,Triangle>>>>(headerMap.entrySet());

        for(Map.Entry<Integer,java.util.List<Map.Entry<Double,Triangle>>> entryForAll : listHeaderForMap){
            java.util.List<Map.Entry<Double,Triangle>> list =  entryForAll.getValue();
            java.util.List<Pnt> tmpListOfPnt = new ArrayList<Pnt>();
            int headerIndex = entryForAll.getKey();
            //for(java.util.List<Map.Entry<Double,Triangle>> list : headerMap.values()){
            int[] map = new int[vertices.length];
            for(int i = 0; i < list.size(); i++) {
                Triangle triTemp = list.get(i).getValue();
                int Point_1_index = triTemp.get(0).getIndex();
                int Point_2_index = triTemp.get(1).getIndex();
                int Point_3_index = triTemp.get(2).getIndex();
                map[Point_1_index]++;
                map[Point_2_index]++;
                map[Point_3_index]++;
                System.out.println("[" + Point_1_index + "," + Point_2_index + "," + Point_3_index + "]");
            }
            boolean isEdgeHeader = false;
            for(int j = 0;j < map.length;j++){
                if(map[j] == 1) {isEdgeHeader = true;break;}
                else{
                    continue;
                } 
            }
            if(!isEdgeHeader){
                System.out.println("enter;;");
                Pnt pFrom,pTo,pointOnRectangle;
                for(int i = 0;i < list.size();i++){
                    pFrom = list.get(i).getValue().getCircumcenter();
                    if(isInRectangle(pFrom)){
                        tmpListOfPnt.add(pFrom);
                    }
                }
                for(int i = 0;i < list.size() - 1;i++){
                    pFrom = list.get(i).getValue().getCircumcenter();
                    pTo = list.get(i + 1).getValue().getCircumcenter();
                    if(isInRectangle(pFrom) ^ isInRectangle(pTo)){
                        if(getIntersectPoint(pFrom,pTo,point1,point2) != null) 
                            pointOnRectangle = getIntersectPoint(pFrom,pTo,point1,point2);
                        else if(getIntersectPoint(pFrom,pTo,point2,point3) != null) 
                            pointOnRectangle = getIntersectPoint(pFrom,pTo,point2,point3);
                        else if(getIntersectPoint(pFrom,pTo,point3,point4) != null) 
                            pointOnRectangle = getIntersectPoint(pFrom,pTo,point3,point4);
                        else 
                            pointOnRectangle = getIntersectPoint(pFrom,pTo,point4,point1);

                    if(pointOnRectangle != null)
                        tmpListOfPnt.add(pointOnRectangle);
                        //System.out.println("[" + Point_1_index + "," + Point_2_index + "," + Point_3_index + "]");
                    }
                    else if(!(isInRectangle(pFrom) || isInRectangle(pTo)) ) {
                        Pnt pointOnRectangle1 = null,pointOnRectangle2 = null,pointOnRectangle3 = null,pointOnRectangle4 = null;
                        if(getIntersectPoint(pFrom,pTo,point1,point2) != null)
                            pointOnRectangle1 = getIntersectPoint(pFrom,pTo,point1,point2);
                        if(getIntersectPoint(pFrom,pTo,point2,point3) != null)
                            pointOnRectangle2 = getIntersectPoint(pFrom,pTo,point2,point3);
                        if(getIntersectPoint(pFrom,pTo,point3,point4) != null)
                            pointOnRectangle3 = getIntersectPoint(pFrom,pTo,point3,point4);
                        if(getIntersectPoint(pFrom,pTo,point4,point1) != null)
                            pointOnRectangle4 = getIntersectPoint(pFrom,pTo,point4,point1);
                        if(pointOnRectangle1 != null) tmpListOfPnt.add(pointOnRectangle1);
                        if(pointOnRectangle2 != null) tmpListOfPnt.add(pointOnRectangle2);
                        if(pointOnRectangle3 != null) tmpListOfPnt.add(pointOnRectangle3);
                        if(pointOnRectangle4 != null) tmpListOfPnt.add(pointOnRectangle4);
                    }

                }
                pFrom = list.get(list.size() - 1).getValue().getCircumcenter();
                pTo = list.get(0).getValue().getCircumcenter();
                if(isInRectangle(pFrom) ^ isInRectangle(pTo)){
                    if(getIntersectPoint(pFrom,pTo,point1,point2) != null) 
                        pointOnRectangle = getIntersectPoint(pFrom,pTo,point1,point2);
                    else if(getIntersectPoint(pFrom,pTo,point2,point3) != null) 
                        pointOnRectangle = getIntersectPoint(pFrom,pTo,point2,point3);
                    else if(getIntersectPoint(pFrom,pTo,point3,point4) != null) 
                        pointOnRectangle = getIntersectPoint(pFrom,pTo,point3,point4);
                    else 
                        pointOnRectangle = getIntersectPoint(pFrom,pTo,point4,point1);
                    if(pointOnRectangle != null)
                    tmpListOfPnt.add(pointOnRectangle);
                }
                else if(!(isInRectangle(pFrom) || isInRectangle(pTo)) ) {
                    Pnt pointOnRectangle1 = null,pointOnRectangle2 = null,pointOnRectangle3 = null,pointOnRectangle4 = null;
                    if(getIntersectPoint(pFrom,pTo,point1,point2) != null)
                        pointOnRectangle1 = getIntersectPoint(pFrom,pTo,point1,point2);
                    if(getIntersectPoint(pFrom,pTo,point2,point3) != null)
                        pointOnRectangle2 = getIntersectPoint(pFrom,pTo,point2,point3);
                    if(getIntersectPoint(pFrom,pTo,point3,point4) != null)
                        pointOnRectangle3 = getIntersectPoint(pFrom,pTo,point3,point4);
                    if(getIntersectPoint(pFrom,pTo,point4,point1) != null)
                        pointOnRectangle4 = getIntersectPoint(pFrom,pTo,point4,point1);
                    if(pointOnRectangle1 != null) tmpListOfPnt.add(pointOnRectangle1);
                    if(pointOnRectangle2 != null) tmpListOfPnt.add(pointOnRectangle2);
                    if(pointOnRectangle3 != null) tmpListOfPnt.add(pointOnRectangle3);
                    if(pointOnRectangle4 != null) tmpListOfPnt.add(pointOnRectangle4);
                }
            }else{
                int[] single = {-1,-1};
                for(int j = 0;j < map.length;j++){
                    if(map[j] == 1 && single[0] == -1 && j != headerIndex) {single[0] = j;}
                    else if(map[j] == 1 && single[0] != -1 && single[1] == -1 && j != headerIndex){single[1] = j;} 
                }
                System.out.println("single[0] = " + single[0]);
                System.out.println("single[1] = " + single[1]);
                Triangle[] triangle = new Triangle[2];
                for(int i = 0; i < list.size(); i++) {
                    Triangle triTemp = list.get(i).getValue();
                    int Point_1_index = triTemp.get(0).getIndex();
                    int Point_2_index = triTemp.get(1).getIndex();
                    int Point_3_index = triTemp.get(2).getIndex();
                    if(Point_1_index == single[0] || Point_2_index == single[0] || Point_3_index == single[0]) {
                        triangle[0] = triTemp; 
                    }
                    if(Point_1_index == single[1] || Point_2_index == single[1] || Point_3_index == single[1]) {
                        triangle[1] = triTemp; 
                    }
                }
                            System.out.println("*************************\n" + headerIndex + "*************************");
                for(int q = 0; q < 2;q++){
                    Pnt pFrom1,pTo1;
                    if(triangle[q] != null){
                        pFrom1 = triangle[q].getCircumcenter();
                        if(isInRectangle(pFrom1)) {
                            pTo1 = triangle[q].getPointInEdge(single[q],headerIndex,point1,point2,point3,point4);
                            System.out.println("single" + q + " : " + single[q] );
                            System.out.println(pTo1);
                            
                            System.out.println("debug001");
                            tmpListOfPnt.add(pTo1);
                        }
                    }

                }

                Pnt pFrom,pTo,pointOnRectangle;
                for(int i = 0;i < list.size();i++){
                    pFrom = list.get(i).getValue().getCircumcenter();
                    if(isInRectangle(pFrom)){
                        tmpListOfPnt.add(pFrom);
                            System.out.println("debug002");
                    }
                }
                for(int i = 0;i < list.size() - 1;i++){
                    pFrom = list.get(i).getValue().getCircumcenter();
                    pTo = list.get(i + 1).getValue().getCircumcenter();
                    System.out.println("isInRectangle(pFrom) ^ isInRectangle(pTo)" + (isInRectangle(pFrom) ^ isInRectangle(pTo)));
                    System.out.println("!(isEdgePoint(single,list.get(i).getValue()) && isEdgePoint(single,list.get(i + 1).getValue()))" + (!(isEdgePoint(single,list.get(i).getValue()) && isEdgePoint(single,list.get(i + 1).getValue()))));
                    if((isInRectangle(pFrom) ^ isInRectangle(pTo)) && (!(isEdgePoint(single,list.get(i).getValue()) && isEdgePoint(single,list.get(i + 1).getValue())) || list.size() <= 2)){
                        System.out.println("enter one if");
                        if(getIntersectPoint(pFrom,pTo,point1,point2) != null) 
                            pointOnRectangle = getIntersectPoint(pFrom,pTo,point1,point2);
                        else if(getIntersectPoint(pFrom,pTo,point2,point3) != null) 
                            pointOnRectangle = getIntersectPoint(pFrom,pTo,point2,point3);
                        else if(getIntersectPoint(pFrom,pTo,point3,point4) != null) 
                            pointOnRectangle = getIntersectPoint(pFrom,pTo,point3,point4);
                        else 
                            pointOnRectangle = getIntersectPoint(pFrom,pTo,point4,point1);

                            System.out.println("debug003");
                    if(pointOnRectangle != null)
                        tmpListOfPnt.add(pointOnRectangle);
                    }
                    //else if(!(isInRectangle(pFrom) || isInRectangle(pTo)) && (((pFrom.getCoordinates()[0] - 50) * (pTo.getCoordinates()[0] - 50)) < 0 || ((pFrom.getCoordinates()[0] - 1050) * (pTo.getCoordinates()[0] - 1050)) < 0)&& (!(isEdgePoint(single,list.get(i).getValue()) && isEdgePoint(single,list.get(i + 1).getValue())) || list.size() <= 2)) {
                        else if(!(isInRectangle(pFrom) || isInRectangle(pTo))  && (!(isEdgePoint(single,list.get(i).getValue()) && isEdgePoint(single,list.get(i + 1).getValue())) || list.size() <= 2)) {
                        Pnt pointOnRectangle1 = null,pointOnRectangle2 = null,pointOnRectangle3 = null,pointOnRectangle4 = null;
                        if(getIntersectPoint(pFrom,pTo,point1,point2) != null) 
                            pointOnRectangle1 = getIntersectPoint(pFrom,pTo,point1,point2);
                        if(getIntersectPoint(pFrom,pTo,point2,point3) != null) 
                            pointOnRectangle2 = getIntersectPoint(pFrom,pTo,point2,point3);
                        if(getIntersectPoint(pFrom,pTo,point3,point4) != null) 
                            pointOnRectangle3 = getIntersectPoint(pFrom,pTo,point3,point4);
                        if(getIntersectPoint(pFrom,pTo,point4,point1) != null) 
                            pointOnRectangle4 = getIntersectPoint(pFrom,pTo,point4,point1);
                        if(pointOnRectangle1 != null) tmpListOfPnt.add(pointOnRectangle1);
                        if(pointOnRectangle2 != null) tmpListOfPnt.add(pointOnRectangle2);
                        if(pointOnRectangle3 != null) tmpListOfPnt.add(pointOnRectangle3);
                        if(pointOnRectangle4 != null) tmpListOfPnt.add(pointOnRectangle4);
                    }
                }
                pFrom = list.get(list.size() - 1).getValue().getCircumcenter();
                pTo = list.get(0).getValue().getCircumcenter();
                if((isInRectangle(pFrom) ^ isInRectangle(pTo)) && (!(isEdgePoint(single,list.get(list.size() - 1).getValue()) && isEdgePoint(single,list.get(0).getValue()))) || list.size() <= 2){
                        System.out.println("enter two if");
                    if(getIntersectPoint(pFrom,pTo,point1,point2) != null) 
                        pointOnRectangle = getIntersectPoint(pFrom,pTo,point1,point2);
                    else if(getIntersectPoint(pFrom,pTo,point2,point3) != null) 
                        pointOnRectangle = getIntersectPoint(pFrom,pTo,point2,point3);
                    else if(getIntersectPoint(pFrom,pTo,point3,point4) != null) 
                        pointOnRectangle = getIntersectPoint(pFrom,pTo,point3,point4);
                    else 
                        pointOnRectangle = getIntersectPoint(pFrom,pTo,point4,point1);
                    if(pointOnRectangle != null)
                    tmpListOfPnt.add(pointOnRectangle);
                }
                else if(!(isInRectangle(pFrom) || isInRectangle(pTo))  && (!(isEdgePoint(single,list.get(list.size() - 1).getValue()) && isEdgePoint(single,list.get(0).getValue()))) || list.size() <= 2) {
                    Pnt pointOnRectangle1 = null,pointOnRectangle2 = null,pointOnRectangle3 = null,pointOnRectangle4 = null;
                    if(getIntersectPoint(pFrom,pTo,point1,point2) != null)
                        pointOnRectangle1 = getIntersectPoint(pFrom,pTo,point1,point2);
                    if(getIntersectPoint(pFrom,pTo,point2,point3) != null)
                        pointOnRectangle2 = getIntersectPoint(pFrom,pTo,point2,point3);
                    if(getIntersectPoint(pFrom,pTo,point3,point4) != null)
                        pointOnRectangle3 = getIntersectPoint(pFrom,pTo,point3,point4);
                    if(getIntersectPoint(pFrom,pTo,point4,point1) != null)
                        pointOnRectangle4 = getIntersectPoint(pFrom,pTo,point4,point1);
                    if(pointOnRectangle1 != null) tmpListOfPnt.add(pointOnRectangle1);
                    if(pointOnRectangle2 != null) tmpListOfPnt.add(pointOnRectangle2);
                    if(pointOnRectangle3 != null) tmpListOfPnt.add(pointOnRectangle3);
                    if(pointOnRectangle4 != null) tmpListOfPnt.add(pointOnRectangle4);
                }




                //int x50,x1050,y50,y1050 = 0;
                int x50 = 0;
                int x1050 = 0;
                int y50 = 0;
                int y1050 = 0;
                for(Pnt pntTemp : tmpListOfPnt){
                    System.out.println(pntTemp);
                    if(pntTemp.getCoordinates()[0] > 49.99999 && pntTemp.getCoordinates()[0] < 50.00001) {
                        x50++;
                        System.out.println("&&&&&" + pntTemp.getCoordinates()[0]);
                    }
                    else if(pntTemp.getCoordinates()[0] > 1049.99999 && pntTemp.getCoordinates()[0] < 1050.00001) x1050++; 
                     
                    if(pntTemp.getCoordinates()[1] > 49.99999 && pntTemp.getCoordinates()[1] < 50.00001) y50++;
                    else if(pntTemp.getCoordinates()[1] > 1049.99999 && pntTemp.getCoordinates()[1] < 1050.00001) y1050++; 
                }
                if(x50 > 0 && y50 > 0){
                    System.out.println("debug005");
                    tmpListOfPnt.add(new Pnt(0,50,50)); 
                }else if(x50 > 0 && y1050 > 0){
                    System.out.println("debug005");
                    tmpListOfPnt.add(new Pnt(0,50,1050)); 
                }else if(x1050 > 0 && y1050 > 0){
                    System.out.println("debug005");
                    tmpListOfPnt.add(new Pnt(0,1050,1050)); 
                }else if(x1050 > 0 && y50 > 0){
                    System.out.println("debug005");
                    tmpListOfPnt.add(new Pnt(0,1050,50)); 
                }
            }

            headerAndPoints.put(headerIndex,getPointsOfOneHeader(new Pnt(headerIndex,vertices[headerIndex][0],vertices[headerIndex][1]),tmpListOfPnt));
            headerAndNormalPoints.put(headerIndex,getNormalPointsOfOneHeader(new Pnt(headerIndex,vertices[headerIndex][0],vertices[headerIndex][1])));

            }

    } 
    public void drawThiessenPolygons2(Graphics g){
        Graphics2D g2 = (Graphics2D) g;
        BasicStroke stroke =new BasicStroke(1);
        g2.setStroke(stroke);
        g.setColor(Color.lightGray);
    
    }
    public void drawThiessenPolygons(Graphics g){

        Graphics2D g2 = (Graphics2D) g;
        BasicStroke stroke =new BasicStroke(1);
        g2.setStroke(stroke);
        g.setColor(Color.lightGray);
        /*
          for(java.util.List<Map.Entry<Double,Triangle>> list : headerMap.values()){
          int[] map = new int[vertices.length];
          for(int i = 0; i < list.size(); i++) {
          Triangle triTemp = list.get(i).getValue();
          int Point_1_index = triTemp.get(0).getIndex();
          int Point_2_index = triTemp.get(1).getIndex();
          int Point_3_index = triTemp.get(2).getIndex();
          map[Point_1_index]++;
          map[Point_2_index]++;
          map[Point_3_index]++;
          System.out.println("[" + Point_1_index + "," + Point_2_index + "," + Point_3_index + "]");
          }
          boolean isEdgeTriangle = false;
          for(int j = 0;j < map.length;j++){
          if(map[j] == 1) {isEdgeTriangle = true;break;}
          else{
          continue;
          } 
          }
          if(!isEdgeTriangle){
          System.out.println("enter;;");
          Pnt pFrom,pTo,pointOnRectangle;
          for(int i = 0;i < list.size() - 1;i++){
          pFrom = list.get(i).getValue().getCircumcenter();
          pTo = list.get(i + 1).getValue().getCircumcenter();
          if(isInRectangle(pFrom) ^ isInRectangle(pTo)){
          if(getIntersectPoint(pFrom,pTo,point1,point2) != null) 
          pointOnRectangle = getIntersectPoint(pFrom,pTo,point1,point2);
          else if(getIntersectPoint(pFrom,pTo,point2,point3) != null) 
          pointOnRectangle = getIntersectPoint(pFrom,pTo,point2,point3);
          else if(getIntersectPoint(pFrom,pTo,point3,point4) != null) 
          pointOnRectangle = getIntersectPoint(pFrom,pTo,point3,point4);
          else 
          pointOnRectangle = getIntersectPoint(pFrom,pTo,point4,point1);

        //System.out.println("[" + Point_1_index + "," + Point_2_index + "," + Point_3_index + "]");
        if(pointOnRectangle != null){
        System.out.println("is not null");
        if(isInRectangle(pFrom) )
        g.drawLine((int)pFrom.getCoordinates()[0],(int)pFrom.getCoordinates()[1],(int)pointOnRectangle.getCoordinates()[0],(int)pointOnRectangle.getCoordinates()[1]); 
        else
        g.drawLine((int)pointOnRectangle.getCoordinates()[0],(int)pointOnRectangle.getCoordinates()[1],(int)pTo.getCoordinates()[0],(int)pTo.getCoordinates()[1]); 
        }
        }else if(isInRectangle(pFrom) && isInRectangle(pTo)){

        g.drawLine((int)pFrom.getCoordinates()[0],(int)pFrom.getCoordinates()[1],(int)pTo.getCoordinates()[0],(int)pTo.getCoordinates()[1]); 
        }

          }
          pFrom = list.get(list.size() - 1).getValue().getCircumcenter();
          pTo = list.get(0).getValue().getCircumcenter();
          if(isInRectangle(pFrom) ^ isInRectangle(pTo)){
          if(getIntersectPoint(pFrom,pTo,point1,point2) != null) 
          pointOnRectangle = getIntersectPoint(pFrom,pTo,point1,point2);
          else if(getIntersectPoint(pFrom,pTo,point2,point3) != null) 
          pointOnRectangle = getIntersectPoint(pFrom,pTo,point2,point3);
          else if(getIntersectPoint(pFrom,pTo,point3,point4) != null) 
          pointOnRectangle = getIntersectPoint(pFrom,pTo,point3,point4);
          else 
          pointOnRectangle = getIntersectPoint(pFrom,pTo,point4,point1);
          if(pointOnRectangle != null){
          if(isInRectangle(pFrom))
          g.drawLine((int)pFrom.getCoordinates()[0],(int)pFrom.getCoordinates()[1],(int)pointOnRectangle.getCoordinates()[0],(int)pointOnRectangle.getCoordinates()[1]); 
          else
          g.drawLine((int)pointOnRectangle.getCoordinates()[0],(int)pointOnRectangle.getCoordinates()[1],(int)pTo.getCoordinates()[0],(int)pTo.getCoordinates()[1]); 
          }
          }else {

          g.drawLine((int)pFrom.getCoordinates()[0],(int)pFrom.getCoordinates()[1],(int)pTo.getCoordinates()[0],(int)pTo.getCoordinates()[1]); 
          }
        */
            java.util.List<Map.Entry<Integer,java.util.List<Map.Entry<Double,Triangle>>>> listHeaderForMap = new ArrayList<Map.Entry<Integer,java.util.List<Map.Entry<Double,Triangle>>>>(headerMap.entrySet());

        for(Map.Entry<Integer,java.util.List<Map.Entry<Double,Triangle>>> entryForAll : listHeaderForMap){
            java.util.List<Map.Entry<Double,Triangle>> list =  entryForAll.getValue();
            int headerIndex = entryForAll.getKey();
            //for(java.util.List<Map.Entry<Double,Triangle>> list : headerMap.values()){
            int[] map = new int[vertices.length];
            for(int i = 0; i < list.size(); i++) {
                Triangle triTemp = list.get(i).getValue();
                int Point_1_index = triTemp.get(0).getIndex();
                int Point_2_index = triTemp.get(1).getIndex();
                int Point_3_index = triTemp.get(2).getIndex();
                map[Point_1_index]++;
                map[Point_2_index]++;
                map[Point_3_index]++;
                System.out.println("[" + Point_1_index + "," + Point_2_index + "," + Point_3_index + "]");
            }
            boolean isEdgeHeader = false;
            for(int j = 0;j < map.length;j++){
                if(map[j] == 1) {isEdgeHeader = true;break;}
                else{
                    continue;
                } 
            }
            if(!isEdgeHeader){
                System.out.println("enter;;");
                Pnt pFrom,pTo,pointOnRectangle;
                for(int i = 0;i < list.size();i++){
                    pFrom = list.get(i).getValue().getCircumcenter();
                }
                for(int i = 0;i < list.size() - 1;i++){
                    pFrom = list.get(i).getValue().getCircumcenter();
                    pTo = list.get(i + 1).getValue().getCircumcenter();
                    if(isInRectangle(pFrom) ^ isInRectangle(pTo)){
                        if(getIntersectPoint(pFrom,pTo,point1,point2) != null) 
                            pointOnRectangle = getIntersectPoint(pFrom,pTo,point1,point2);
                        else if(getIntersectPoint(pFrom,pTo,point2,point3) != null) 
                            pointOnRectangle = getIntersectPoint(pFrom,pTo,point2,point3);
                        else if(getIntersectPoint(pFrom,pTo,point3,point4) != null) 
                            pointOnRectangle = getIntersectPoint(pFrom,pTo,point3,point4);
                        else 
                            pointOnRectangle = getIntersectPoint(pFrom,pTo,point4,point1);

                        //System.out.println("[" + Point_1_index + "," + Point_2_index + "," + Point_3_index + "]");
                        if(pointOnRectangle != null){
                            System.out.println("is not null");
                            if(isInRectangle(pFrom) )
                                g.drawLine((int)pFrom.getCoordinates()[0],(int)pFrom.getCoordinates()[1],(int)pointOnRectangle.getCoordinates()[0],(int)pointOnRectangle.getCoordinates()[1]); 
                            else
                                g.drawLine((int)pointOnRectangle.getCoordinates()[0],(int)pointOnRectangle.getCoordinates()[1],(int)pTo.getCoordinates()[0],(int)pTo.getCoordinates()[1]); 
                        }
                    }else if(isInRectangle(pFrom) && isInRectangle(pTo)){

                        g.drawLine((int)pFrom.getCoordinates()[0],(int)pFrom.getCoordinates()[1],(int)pTo.getCoordinates()[0],(int)pTo.getCoordinates()[1]); 
                    }
                    else if(!(isInRectangle(pFrom) || isInRectangle(pTo)) ) {
                        Pnt pointOnRectangle1 = null,pointOnRectangle2 = null,pointOnRectangle3 = null,pointOnRectangle4 = null;
                        if(getIntersectPoint(pFrom,pTo,point1,point2) != null)
                            pointOnRectangle1 = getIntersectPoint(pFrom,pTo,point1,point2);
                        if(getIntersectPoint(pFrom,pTo,point2,point3) != null)
                            pointOnRectangle2 = getIntersectPoint(pFrom,pTo,point2,point3);
                        if(getIntersectPoint(pFrom,pTo,point3,point4) != null)
                            pointOnRectangle3 = getIntersectPoint(pFrom,pTo,point3,point4);
                        if(getIntersectPoint(pFrom,pTo,point4,point1) != null)
                            pointOnRectangle4 = getIntersectPoint(pFrom,pTo,point4,point1);
                        List<Pnt> abc = new ArrayList<Pnt>();
                        if(pointOnRectangle1 != null) abc.add(pointOnRectangle1);
                        if(pointOnRectangle2 != null) abc.add(pointOnRectangle2);
                        if(pointOnRectangle3 != null) abc.add(pointOnRectangle3);
                        if(pointOnRectangle4 != null) abc.add(pointOnRectangle4);
                        if(abc.size() == 2)
                        g.drawLine((int)abc.get(0).getCoordinates()[0],(int)abc.get(0).getCoordinates()[1],(int)abc.get(1).getCoordinates()[0],(int)abc.get(1).getCoordinates()[1]);
                    }

                }
                pFrom = list.get(list.size() - 1).getValue().getCircumcenter();
                pTo = list.get(0).getValue().getCircumcenter();
                if(isInRectangle(pFrom) ^ isInRectangle(pTo)){
                    if(getIntersectPoint(pFrom,pTo,point1,point2) != null) 
                        pointOnRectangle = getIntersectPoint(pFrom,pTo,point1,point2);
                    else if(getIntersectPoint(pFrom,pTo,point2,point3) != null) 
                        pointOnRectangle = getIntersectPoint(pFrom,pTo,point2,point3);
                    else if(getIntersectPoint(pFrom,pTo,point3,point4) != null) 
                        pointOnRectangle = getIntersectPoint(pFrom,pTo,point3,point4);
                    else 
                        pointOnRectangle = getIntersectPoint(pFrom,pTo,point4,point1);
                    if(pointOnRectangle != null){
                        if(isInRectangle(pFrom))
                            g.drawLine((int)pFrom.getCoordinates()[0],(int)pFrom.getCoordinates()[1],(int)pointOnRectangle.getCoordinates()[0],(int)pointOnRectangle.getCoordinates()[1]); 
                        else
                            g.drawLine((int)pointOnRectangle.getCoordinates()[0],(int)pointOnRectangle.getCoordinates()[1],(int)pTo.getCoordinates()[0],(int)pTo.getCoordinates()[1]); 
                    }
                }else if(isInRectangle(pFrom) && isInRectangle(pTo)){

                    g.drawLine((int)pFrom.getCoordinates()[0],(int)pFrom.getCoordinates()[1],(int)pTo.getCoordinates()[0],(int)pTo.getCoordinates()[1]); 
                }
                else if(!(isInRectangle(pFrom) || isInRectangle(pTo)) ) {
                    Pnt pointOnRectangle1 = null,pointOnRectangle2 = null,pointOnRectangle3 = null,pointOnRectangle4 = null;
                    if(getIntersectPoint(pFrom,pTo,point1,point2) != null)
                        pointOnRectangle1 = getIntersectPoint(pFrom,pTo,point1,point2);
                    if(getIntersectPoint(pFrom,pTo,point2,point3) != null)
                        pointOnRectangle2 = getIntersectPoint(pFrom,pTo,point2,point3);
                    if(getIntersectPoint(pFrom,pTo,point3,point4) != null)
                        pointOnRectangle3 = getIntersectPoint(pFrom,pTo,point3,point4);
                    if(getIntersectPoint(pFrom,pTo,point4,point1) != null)
                        pointOnRectangle4 = getIntersectPoint(pFrom,pTo,point4,point1);
                    List<Pnt> abc = new ArrayList<Pnt>();
                    if(pointOnRectangle1 != null) abc.add(pointOnRectangle1);
                    if(pointOnRectangle2 != null) abc.add(pointOnRectangle2);
                    if(pointOnRectangle3 != null) abc.add(pointOnRectangle3);
                    if(pointOnRectangle4 != null) abc.add(pointOnRectangle4);
                    if(abc.size() == 2)
                    g.drawLine((int)abc.get(0).getCoordinates()[0],(int)abc.get(0).getCoordinates()[1],(int)abc.get(1).getCoordinates()[0],(int)abc.get(1).getCoordinates()[1]);
                }
                /*
                  Pnt pFrom,pTo;
                  for(int q = 0;q < list.size() - 1;q++){
                  pFrom = list.get(q).getValue().getCircumcenter();
                  pTo = list.get(q + 1).getValue().getCircumcenter();

                  g.drawLine((int)pFrom.getCoordinates()[0],(int)pFrom.getCoordinates()[1],(int)pTo.getCoordinates()[0],(int)pTo.getCoordinates()[1]); 

                  }
                  pFrom = list.get(list.size() - 1).getValue().getCircumcenter();
                  pTo = list.get(0).getValue().getCircumcenter();

                  g.drawLine((int)pFrom.getCoordinates()[0],(int)pFrom.getCoordinates()[1],(int)pTo.getCoordinates()[0],(int)pTo.getCoordinates()[1]); 
                //break; 
                System.out.println();
                System.out.println();
                 */ 
            }else {
                int[] single = {-1,-1};
                for(int j = 0;j < map.length;j++){
                    if(map[j] == 1 && single[0] == -1 && j != headerIndex) {single[0] = j;}
                    else if(map[j] == 1 && single[0] != -1 && single[1] == -1 && j != headerIndex){single[1] = j;} 
                }
                System.out.println("single[0] = " + single[0]);
                System.out.println("single[1] = " + single[1]);
                Triangle[] triangle = new Triangle[2];
                for(int i = 0; i < list.size(); i++) {
                    Triangle triTemp = list.get(i).getValue();
                    int Point_1_index = triTemp.get(0).getIndex();
                    int Point_2_index = triTemp.get(1).getIndex();
                    int Point_3_index = triTemp.get(2).getIndex();
                    if(Point_1_index == single[0] || Point_2_index == single[0] || Point_3_index == single[0]) {
                        triangle[0] = triTemp; 
                    }
                    if(Point_1_index == single[1] || Point_2_index == single[1] || Point_3_index == single[1]) {
                        triangle[1] = triTemp; 
                    }
                }
                            System.out.println("*************************\n" + headerIndex + "*************************");
                for(int q = 0; q < 2;q++){
                    Pnt pFrom1,pTo1;
                    if(triangle[q] != null){
                        pFrom1 = triangle[q].getCircumcenter();
                        if(isInRectangle(pFrom1)) {
                            pTo1 = triangle[q].getPointInEdge(single[q],headerIndex,point1,point2,point3,point4);
                            System.out.println("single" + q + " : " + single[q] );
                            System.out.println(pTo1);
                            
                            System.out.println("debug001");
                            g.drawLine((int)pFrom1.getCoordinates()[0],(int)pFrom1.getCoordinates()[1],(int)pTo1.getCoordinates()[0],(int)pTo1.getCoordinates()[1]); 
                        }
                    }

                }





                Pnt pFrom,pTo,pointOnRectangle;
                for(int i = 0;i < list.size();i++){
                    pFrom = list.get(i).getValue().getCircumcenter();
                }
                for(int i = 0;i < list.size() - 1;i++){
                    pFrom = list.get(i).getValue().getCircumcenter();
                    pTo = list.get(i + 1).getValue().getCircumcenter();
                    System.out.println("isInRectangle(pFrom) ^ isInRectangle(pTo)" + (isInRectangle(pFrom) ^ isInRectangle(pTo)));
                    System.out.println("!(isEdgePoint(single,list.get(i).getValue()) && isEdgePoint(single,list.get(i + 1).getValue()))" + (!(isEdgePoint(single,list.get(i).getValue()) && isEdgePoint(single,list.get(i + 1).getValue()))));
                    if((isInRectangle(pFrom) ^ isInRectangle(pTo)) && (!(isEdgePoint(single,list.get(i).getValue()) && isEdgePoint(single,list.get(i + 1).getValue())) || list.size() <= 2)){
                        System.out.println("enter one if");
                        if(getIntersectPoint(pFrom,pTo,point1,point2) != null) 
                            pointOnRectangle = getIntersectPoint(pFrom,pTo,point1,point2);
                        else if(getIntersectPoint(pFrom,pTo,point2,point3) != null) 
                            pointOnRectangle = getIntersectPoint(pFrom,pTo,point2,point3);
                        else if(getIntersectPoint(pFrom,pTo,point3,point4) != null) 
                            pointOnRectangle = getIntersectPoint(pFrom,pTo,point3,point4);
                        else 
                            pointOnRectangle = getIntersectPoint(pFrom,pTo,point4,point1);

                            System.out.println("debug003");
                        //System.out.println("[" + Point_1_index + "," + Point_2_index + "," + Point_3_index + "]");
                        if(pointOnRectangle != null){
                            System.out.println("is not null");
                            if(isInRectangle(pFrom) )
                                g.drawLine((int)pFrom.getCoordinates()[0],(int)pFrom.getCoordinates()[1],(int)pointOnRectangle.getCoordinates()[0],(int)pointOnRectangle.getCoordinates()[1]); 
                            else
                                g.drawLine((int)pointOnRectangle.getCoordinates()[0],(int)pointOnRectangle.getCoordinates()[1],(int)pTo.getCoordinates()[0],(int)pTo.getCoordinates()[1]); 
                        }
                    }else if(isInRectangle(pFrom) && isInRectangle(pTo) && !(isEdgePoint(single,list.get(i).getValue()) && isEdgePoint(single,list.get(i + 1).getValue()))){

                        g.drawLine((int)pFrom.getCoordinates()[0],(int)pFrom.getCoordinates()[1],(int)pTo.getCoordinates()[0],(int)pTo.getCoordinates()[1]); 
                    }
                }
                pFrom = list.get(list.size() - 1).getValue().getCircumcenter();
                pTo = list.get(0).getValue().getCircumcenter();
                if((isInRectangle(pFrom) ^ isInRectangle(pTo)) && (!(isEdgePoint(single,list.get(list.size() - 1).getValue()) && isEdgePoint(single,list.get(0).getValue()))) || list.size() <= 2){
                        System.out.println("enter two if");
                    if(getIntersectPoint(pFrom,pTo,point1,point2) != null) 
                        pointOnRectangle = getIntersectPoint(pFrom,pTo,point1,point2);
                    else if(getIntersectPoint(pFrom,pTo,point2,point3) != null) 
                        pointOnRectangle = getIntersectPoint(pFrom,pTo,point2,point3);
                    else if(getIntersectPoint(pFrom,pTo,point3,point4) != null) 
                        pointOnRectangle = getIntersectPoint(pFrom,pTo,point3,point4);
                    else 
                        pointOnRectangle = getIntersectPoint(pFrom,pTo,point4,point1);
                    if(pointOnRectangle != null){
                            System.out.println("debug004");
                        if(isInRectangle(pFrom))
                            g.drawLine((int)pFrom.getCoordinates()[0],(int)pFrom.getCoordinates()[1],(int)pointOnRectangle.getCoordinates()[0],(int)pointOnRectangle.getCoordinates()[1]); 
                        else
                            g.drawLine((int)pointOnRectangle.getCoordinates()[0],(int)pointOnRectangle.getCoordinates()[1],(int)pTo.getCoordinates()[0],(int)pTo.getCoordinates()[1]); 
                    }
                }else if(isInRectangle(pFrom) && isInRectangle(pTo) && !(isEdgePoint(single,list.get(list.size() - 1).getValue()) && isEdgePoint(single,list.get(0).getValue()))){

                    g.drawLine((int)pFrom.getCoordinates()[0],(int)pFrom.getCoordinates()[1],(int)pTo.getCoordinates()[0],(int)pTo.getCoordinates()[1]); 
                }



            }
            }
            } 
        public boolean isEdgePoint(int[] single, Triangle triTemp){
            boolean flag = false;
                int Point_1_index = triTemp.get(0).getIndex();
                int Point_2_index = triTemp.get(1).getIndex();
                int Point_3_index = triTemp.get(2).getIndex();
                //System.out.println("index1 " + Point_1_index);
                //System.out.println("index2 " + Point_2_index);
                //System.out.println("index3 " + Point_3_index);
                for(int i : single){
                //System.out.println("single " + i);
                    if(Point_1_index == i || Point_2_index == i || Point_3_index == i){
                        flag = true;
                        break;
                    }
                }
                //System.out.println();
            return flag;
        
        }
        public void drawTotalTriangle(Graphics g){
            Graphics2D g2 = (Graphics2D) g;
            BasicStroke stroke =new BasicStroke(1);
            g2.setStroke(stroke);
            g.setColor(Color.black);
            /*
              for(int i : clusterHeaders){
              TreeMap<Double,Integer> temp = new TreeMap<Double,Integer>();
              int[] clusterWithOneHeader = new int[vertices.length];
              int flag = 0;
              g.drawString(argument_n[i] + "", vertices[i][0] - 2, vertices[i][1] -2);
              for(int j = 0; j < vertices.length;j++){
              if(linkedHeaders[i][j] == 1){
              flag++; 
              temp.put(weightBetweenTwoHeaders(i,j),j);
              }
              } 
              if(flag > argument_n[i]){
              java.util.List<Map.Entry<Double,Integer>> list = new ArrayList<Map.Entry<Double,Integer>>(temp.entrySet());
              for(int l = 0;l < flag - argument_n[i];l++){
              linkedHeaders[i][list.get(0).getValue()] = 0;
              linkedHeaders[list.get(0).getValue()][i] = 0;
              list.remove(0);
              }
              }
              }*/
            for(int i = 0; i < vertices.length;i++)
                for(int j = 0;j < vertices.length;j++)
                    if(linkedHeaders[i][j] == 1) {
                        g.drawLine(vertices[i][0] + 2, vertices[i][1] + 2, vertices[j][0] + 2, vertices[j][1] + 2);
                    }
        }
        public void dealWithTriangle(){
            for(int i : clusterHeaders){
                TreeMap<Double,Integer> temp = new TreeMap<Double,Integer>();
                int[] clusterWithOneHeader = new int[vertices.length];
                int flag = 0;
                for(int j = 0; j < vertices.length;j++){
                    if(linkedHeaders[i][j] == 1){
                        flag++; 
                        temp.put(weightBetweenTwoHeaders(i,j),j);
                    }
                } 
                if(flag > argument_n[i]){
                    java.util.List<Map.Entry<Double,Integer>> list = new ArrayList<Map.Entry<Double,Integer>>(temp.entrySet());
                    for(int l = 0;l < flag - argument_n[i];l++){
                        linkedHeaders[i][list.get(0).getValue()] = 0;
                        linkedHeaders[list.get(0).getValue()][i] = 0;
                        list.remove(0);
                    }
                }
            }
        }
        public void drawTriangle(Graphics g){
            Graphics2D g2 = (Graphics2D) g;
            BasicStroke stroke =new BasicStroke(2);
            g2.setStroke(stroke);
            g.setColor(Color.blue);
            for(int i = 0; i < vertices.length;i++)
                for(int j = 0;j < vertices.length;j++)
                    if(linkedHeaders[i][j] == 1) {
                        g.drawLine(vertices[i][0] + 2, vertices[i][1] + 2, vertices[j][0] + 2, vertices[j][1] + 2);
                    }
            g.setColor(Color.red);
            for(int i : clusterHeaders){
                g.drawString(argument_n[i] + "", vertices[i][0] + 5, vertices[i][1] +5);
            }

        }
        //算法具体实现
        public void newGraph(){
            System.out.println("==============================\n===============================");
            verticesInCluster = new HashSet<Integer>();
            resultOfCluster = new ArrayList<ArrayList<Integer>>();
            clusterHeaders = new ArrayList<Integer>();
            trueNodelist = new ArrayList<>();
            headerMap.clear();
            headerAndPoints.clear();
            headerAndNormalPoints.clear();
            vertices = new int[NUMBEROFVERTICES][2];
            params = new int[vertices.length][2];
            linked = new int[vertices.length][vertices.length];
            starLinked = new int[vertices.length][vertices.length];
            argument_n = new int[vertices.length];
            argument_e = new int[vertices.length];
            linkedHeaders = new int[vertices.length][vertices.length];
            //For vertices
            for (int i = 0;i < vertices.length;i++) {
                vertices[i][0]	= rnd.nextInt(1000) + 50;
                vertices[i][1]	= rnd.nextInt(1000) + 50;
            }
            //For n
            for (int i = 0;i < vertices.length;i++) {
                argument_n[i] = rnd.nextInt(N2 - N1 + 1) + N1;
            }
            //For e
            for (int i = 0;i < vertices.length;i++) {
                argument_e[i] = rnd.nextInt(E2 - E1 + 1) + E1;
            }
            for(int i = 0;i < vertices.length;i++) {
                ArrayList<Integer> cluster = new ArrayList<Integer>();
                for(int j = 0;j < vertices.length;j++) {

                    if(i != j) {
                        double d = lengthOfLine(i,j,vertices);

                        if(d < D0) {
                            cluster.add(j);
                        }
                    }
                }
                boolean flag1 = true;//如果自己最大则为true
                int me = argument_n[i] * argument_e[i];
                for(int l = 0; l < cluster.size();l++){
                    int other = argument_n[cluster.get(l)] * argument_e[cluster.get(l)];
                    if(me < other) { flag1 = false; break; }
                }
                int maxVerticeIndexInCluster = -1;
                if(flag1) maxVerticeIndexInCluster = i;
                else { 
                    TreeMap<Double,Integer> sortOfCluster = new TreeMap<Double,Integer>(); 
                    for(int l = 0;l < cluster.size();l++){
                    sortOfCluster.put(argument_n[cluster.get(l)] * argument_e[cluster.get(l)] / lengthOfLine(i,cluster.get(l),vertices),cluster.get(l));
                    }
                java.util.List<Map.Entry<Double,Integer>> list = new ArrayList<Map.Entry<Double,Integer>>(sortOfCluster.entrySet());
                Collections.reverse(list);
                int numberOfConnected = 0;
                int oraginalListsize = list.size();
                for(int k = 0;k < oraginalListsize;k++){
                    if(linked[list.get(0).getValue()][i] == 1) break;
                    linked[i][list.get(0).getValue()] = 1;
                    //linked[list.get(0).getValue()][i] = 1;
                    numberOfConnected = numberOfConnectedToVertice(list.get(0).getValue(),linked);
                    verticesInCluster.clear();
                    if(numberOfConnected > C) {
                        linked[i][list.get(0).getValue()] = 0;
                        //linked[list.get(0).getValue()][i] = 0;
                        list.remove(0);
                        continue;
                    }else {
                        //如果建立连接则为1
                        linked[i][list.get(0).getValue()] = 1;
                        //linked[list.get(0).getValue()][i] = 1;
                        if(argument_n[i] > 1) argument_n[i]--;
                        if(argument_n[list.get(0).getValue()] > 0) argument_n[list.get(0).getValue()]--;
                        break;
                    }
                }
                }

            }
            for(int i = 0;i < vertices.length;i++){
                boolean isMainVertice = true;
                for(int j = 0;j < vertices.length;j++) {
                    if(linked[i][j] == 1)  isMainVertice = false;
                }
                if(isMainVertice) {
                    clusterHeaders.add(i);
                }
            }

            Pnt maxVertice1 = new Pnt(-1,-950,50);
            Pnt maxVertice2 = new Pnt(-2,2050,50);
            Pnt maxVertice3 = new Pnt(-3,50,2050);
            Triangle tri =
                new Triangle(maxVertice1,maxVertice2,maxVertice3);
            Triangulation dt = new Triangulation(tri);
            for(int i : clusterHeaders){
                dt.delaunayPlace(new Pnt(i,vertices[i][0],vertices[i][1]));
            }
            Triangle.moreInfo = true;

            Set<Triangle> temp = dt.triGraph.nodeSet();

            for(Triangle s : temp) {
                if(!(s.contains(maxVertice1) || s.contains(maxVertice2) || s.contains(maxVertice3)))  trueNodelist.add(s);
            }
            for(Triangle triTemp : trueNodelist){
                int Point_1_index = triTemp.get(0).getIndex();
                int Point_2_index = triTemp.get(1).getIndex();
                int Point_3_index = triTemp.get(2).getIndex();

                linkedHeaders[Point_1_index][Point_2_index] = 1;
                linkedHeaders[Point_2_index][Point_1_index] = 1;
                linkedHeaders[Point_2_index][Point_3_index] = 1;
                linkedHeaders[Point_3_index][Point_2_index] = 1;
                linkedHeaders[Point_3_index][Point_1_index] = 1;
                linkedHeaders[Point_1_index][Point_3_index] = 1;
            }
            for(int i = 0;i < vertices.length;i++){
                int tmp = 0;
                for(int j = 0;j < vertices.length;j++){
                    if(linkedHeaders[i][j] == 1) tmp++;
                }
                if(tmp > 0) {
                    Pnt currentheader = new Pnt(i,vertices[i][0],vertices[i][1]); 

                    java.util.List<Map.Entry<Double,Triangle>> headerlist = getTrianglesOfPoints(currentheader,trueNodelist);
                    headerMap.put(i,headerlist);
                    //break;
                }

            }
            dealWithThiessenPolygons();
            dealWithTriangle();
            dealWithStar();
            dealWithMatrix();
            //moveAllPoints();
            //moveAllPoints();
            //moveAllPoints();
            //movePoints();
            //movePoints();
            //movePoints();
            //movePoints();
            repaint();
        }
        //计算权值k
        public double weightBetweenTwoHeaders(int i,int j){
            return Math.sqrt(argument_e[i] * argument_e[j]) / lengthOfLine(i,j,vertices);
        }
        //计算所连节点
        public int numberOfConnectedToVertice(int i,int[][] linked){
            verticesInCluster.add(i);
            //verticesInCluster
            for(int p = 0;p < linked.length;p++){
                if((!verticesInCluster.contains(p)) && linked[i][p] == 1) {
                    verticesInCluster.add(p);
                    numberOfConnectedToVertice(p,linked);
                }
                if((!verticesInCluster.contains(p)) && linked[p][i] == 1) {
                    verticesInCluster.add(p);
                    numberOfConnectedToVertice(p,linked);
                }
            }
            return verticesInCluster.size();
        }
        //获取所连接的节点
        public void getConnectedVertices(int i,int[][] linked){
            verticesConnectedToi.add(i);
            //verticesConnectedToi
            for(int p = 0;p < linked.length;p++){
                if((!verticesConnectedToi.contains(p)) && linked[i][p] == 1) {
                    verticesConnectedToi.add(p);
                    getConnectedVertices(p,linked);
                }
                if((!verticesConnectedToi.contains(p)) && linked[p][i] == 1) {
                    verticesConnectedToi.add(p);
                    getConnectedVertices(p,linked);
                }
            }
        }
        public boolean isConnectedVerticesCotainsHeader(HashSet<Integer> clus,ArrayList<Integer> Headers){
            boolean flag = false;
            for(int i : clus){
                if(Headers.contains(i)){
                    flag = true;
                    break; 
                }else{
                    continue; 
                }
            }
            return flag;
        }
        //计算两节点直接距离d
        public double lengthOfLine(int i, int j, int[][] vertices) {
            double width = Math.abs(vertices[i][0] - vertices[j][0]);
            double height = Math.abs(vertices[i][1] - vertices[j][1]);
            return Math.sqrt(width * width + height * height);
        }
        //画箭头
        public static void drawAL(int sx, int sy, int ex, int ey, Graphics2D g2)
        {
            double H = 10;
            double L = 4;
            int x3 = 0;
            int y3 = 0;
            int x4 = 0;
            int y4 = 0;
            double awrad = Math.atan(L / H);
            double arraow_len = Math.sqrt(L * L + H * H);
            double[] arrXY_1 = rotateVec(ex - sx, ey - sy, awrad, true, arraow_len);
            double[] arrXY_2 = rotateVec(ex - sx, ey - sy, -awrad, true, arraow_len);
            double x_3 = ex - arrXY_1[0];
            double y_3 = ey - arrXY_1[1];
            double x_4 = ex - arrXY_2[0];
            double y_4 = ey - arrXY_2[1];

            Double X3 = new Double(x_3);
            x3 = X3.intValue();
            Double Y3 = new Double(y_3);
            y3 = Y3.intValue();
            Double X4 = new Double(x_4);
            x4 = X4.intValue();
            Double Y4 = new Double(y_4);
            y4 = Y4.intValue();
            g2.drawLine(sx, sy, ex, ey);
            GeneralPath triangle = new GeneralPath();
            triangle.moveTo(ex, ey);
            triangle.lineTo(x3, y3);
            triangle.lineTo(x4, y4);
            triangle.closePath();
            g2.fill(triangle);
        }
        public static double[] rotateVec(int px, int py, double ang,  boolean isChLen, double newLen) {
            double mathstr[] = new double[2];
            double vx = px * Math.cos(ang) - py * Math.sin(ang);
            double vy = px * Math.sin(ang) + py * Math.cos(ang);
            if (isChLen) {
                double d = Math.sqrt(vx * vx + vy * vy);
                vx = vx / d * newLen;
                vy = vy / d * newLen;
                mathstr[0] = vx;
                mathstr[1] = vy;
            }
            return mathstr;
        }
        public java.util.List<Pnt> getNormalPointsOfOneHeader(Pnt center){
            java.util.List<Pnt> tmpPnts = new ArrayList<Pnt>();
            java.util.List<Map.Entry<Integer,java.util.List<Pnt>>>  entryHeaderAndPoints= new ArrayList<Map.Entry<Integer,java.util.List<Pnt>>>(headerAndPoints.entrySet());
            for(Map.Entry<Integer,java.util.List<Pnt>> tmpEntry: entryHeaderAndPoints)
            {
                java.util.List<Pnt> polygon = tmpEntry.getValue();
                System.out.println("polygon:" + center.getIndex());
                for(Pnt tmp : polygon){
                System.out.println(tmp);
                }
                int index = tmpEntry.getKey();
                if(center.getIndex() == index){
                    for(int i = 0;i < vertices.length;i++){
                        if(!clusterHeaders.contains(i) && isInPolygon(new Pnt(i,vertices[i][0],vertices[i][1]),polygon)) {
                            tmpPnts.add(new Pnt(i,vertices[i][0],vertices[i][1]));
                        }

                    }
                }  

            }
            System.out.println("tmpPnts: " + tmpPnts.size());
            return tmpPnts;
        }
        public java.util.List<Pnt> getPointsOfOneHeader(Pnt center,java.util.List<Pnt> pnts){
            java.util.List<Pnt> tmpPnts = new ArrayList<Pnt>();
            TreeMap<Double,Pnt> treeMap = new TreeMap<Double,Pnt>();
            for(Pnt tmp : pnts){
                treeMap.put(getDegrees(tmp,center),tmp); 

            }
            java.util.List<Map.Entry<Double,Pnt>> list = new ArrayList<Map.Entry<Double,Pnt>>(treeMap.entrySet());
            Collections.reverse(list);
            for(Map.Entry<Double,Pnt> entrylist : list){
                tmpPnts.add(entrylist.getValue()); 
            }
            return tmpPnts;
        }
        public java.util.List<Map.Entry<Double,Triangle>> getTrianglesOfPoints(Pnt center, ArrayList<Triangle> triTemp){
            //Triangle[] allTri = new Triangle[100];
            //triTemp.toArray(allTri);
            //allTri.trimToSize(); 
            TreeMap<Double,Triangle> treeMap = new TreeMap<Double,Triangle>();

            for(Triangle tmp : triTemp){
                int Point_1_index = tmp.get(0).getIndex();
                int Point_2_index = tmp.get(1).getIndex();
                int Point_3_index = tmp.get(2).getIndex();

                if(Point_1_index == center.getIndex() || Point_2_index == center.getIndex() || Point_3_index == center.getIndex()){
                    Pnt current = tmp.getCircumcenter();
                    treeMap.put(getDegrees(current,center),tmp);
                }
            }
            java.util.List<Map.Entry<Double,Triangle>> list = new ArrayList<Map.Entry<Double,Triangle>>(treeMap.entrySet());
            Collections.reverse(list);
            /*
              for(int i = 0;i < list.size();i++){
              System.out.println(list.get(i).getValue()); 
              System.out.println(list.get(i).getKey()); 
              }
              System.out.println();
              System.out.println();
             */
            return list;

        }
        public double getDegrees(Pnt center, Pnt ori){
            double degrees = 0;
            //Pnt center = getCenterOfCircumcircle(index,tmp);
            double x = (center.getCoordinates()[0] - ori.getCoordinates()[0]);
            double y = (center.getCoordinates()[1] - ori.getCoordinates()[1]);
            //System.out.println("(" + x + "," + y + ")");
            //-270 - 90
            if(x > 0) degrees = Math.toDegrees(Math.atan(y/x));
            else if(x < 0) degrees = Math.toDegrees(Math.atan(y/x) - Math.PI); 
            return degrees;    
        }
        /*
          public Pnt getCenterOfCircumcircle(int index,Triangle triTemp){

          int Point_1_index = triTemp.get(0).getIndex();
          int Point_2_index = triTemp.get(1).getIndex();
          int Point_3_index = triTemp.get(2).getIndex();
          int X = (vertices[Point_1_index][0] + vertices[Point_2_index][0] + vertices[Point_3_index][0])/3;
          int Y = (vertices[Point_1_index][1] + vertices[Point_2_index][1] + vertices[Point_3_index][1])/3;
          Pnt center = new Pnt(index,X,Y);
          return center;
          }*/
        public void moveAllPoints(){
            headerMap.clear();
            headerAndPoints.clear();
            headerAndNormalPoints.clear();
            trueNodelist = new ArrayList<>();
            linkedHeaders = new int[vertices.length][vertices.length];
            starLinked = new int[vertices.length][vertices.length];
            pointNotInNetwork.clear();
            for(int i = 0;i < vertices.length;i++){
                if(clusterHeaders.contains(i)){
                    System.out.print("randomMoveHeader " + i);
                    randomMoveHeader(i);
                }else {
                    randomMoveOrdinaryPoint(i);
                }
            }
            Pnt maxVertice1 = new Pnt(-1,-950,50);
            Pnt maxVertice2 = new Pnt(-2,2050,50);
            Pnt maxVertice3 = new Pnt(-3,50,2050);
            Triangle tri =
                new Triangle(maxVertice1,maxVertice2,maxVertice3);
            Triangulation dt = new Triangulation(tri);
            for(int i : clusterHeaders){
                dt.delaunayPlace(new Pnt(i,vertices[i][0],vertices[i][1]));
            }
            Triangle.moreInfo = true;

            Set<Triangle> temp = dt.triGraph.nodeSet();

            for(Triangle s : temp) {
                if(!(s.contains(maxVertice1) || s.contains(maxVertice2) || s.contains(maxVertice3)))  trueNodelist.add(s);
            }
            for(Triangle triTemp : trueNodelist){
                int Point_1_index = triTemp.get(0).getIndex();
                int Point_2_index = triTemp.get(1).getIndex();
                int Point_3_index = triTemp.get(2).getIndex();

                linkedHeaders[Point_1_index][Point_2_index] = 1;
                linkedHeaders[Point_2_index][Point_1_index] = 1;
                linkedHeaders[Point_2_index][Point_3_index] = 1;
                linkedHeaders[Point_3_index][Point_2_index] = 1;
                linkedHeaders[Point_3_index][Point_1_index] = 1;
                linkedHeaders[Point_1_index][Point_3_index] = 1;
            }
            for(int i = 0;i < vertices.length;i++){
                int tmp = 0;
                for(int j = 0;j < vertices.length;j++){
                    if(linkedHeaders[i][j] == 1) tmp++;
                }
                if(tmp > 0) {
                    Pnt currentheader = new Pnt(i,vertices[i][0],vertices[i][1]); 

                    java.util.List<Map.Entry<Double,Triangle>> headerlist = getTrianglesOfPoints(currentheader,trueNodelist);
                    headerMap.put(i,headerlist);
                    //break;
                }

            }
            dealWithThiessenPolygons();
            dealWithTriangle();
            dealWithStar();

            
                    System.out.println("i============== ");
            java.util.List<Map.Entry<Integer,java.util.List<Pnt>>>  entryHeaderAndPoints= new ArrayList<Map.Entry<Integer,java.util.List<Pnt>>>(headerAndPoints.entrySet());
            for(Map.Entry<Integer,java.util.List<Pnt>> tmpEntry: entryHeaderAndPoints)
            {
                java.util.List<Pnt> polygon = tmpEntry.getValue();
                int index = tmpEntry.getKey();
                    System.out.println("()");
                    for(Pnt pnt : headerAndNormalPoints.get(index)){
                        System.out.print( " " + pnt.getIndex());
                    }
                    System.out.println();
                System.out.println("i============== " + index);
                int[][] tempLinks = new int[vertices.length][vertices.length];
                for(int i = 0;i < vertices.length;i++) {
                    for(int j = 0;j < vertices.length;j++) {
                        tempLinks[i][j] = starLinked[i][j];
                    }
                }
                breakPointsNotInPolygons(index,index,tempLinks,polygon);
            }  
            dealWithMovedStar();
            java.util.List<Integer> indexsOfBreakPoints = new ArrayList<Integer>();

        
            reConnectPointsNotInPolygons();
        
            repaint();

            
        }
        public void movePoints(){
                    System.out.println("i============== ");
            pointNotInNetwork.clear();
            for(int i = 0;i < vertices.length;i++){
                if(clusterHeaders.contains(i)){
                    System.out.print("randomMoveHeader " + i);
                    //randomMoveHeader(i);
                }else {
                    randomMoveOrdinaryPoint(i);
                }
            }
                    System.out.println("i============== ");
            //starLinked = new int[vertices.length][vertices.length];
            java.util.List<Map.Entry<Integer,java.util.List<Pnt>>>  entryHeaderAndPoints= new ArrayList<Map.Entry<Integer,java.util.List<Pnt>>>(headerAndPoints.entrySet());
            for(Map.Entry<Integer,java.util.List<Pnt>> tmpEntry: entryHeaderAndPoints)
            {
                java.util.List<Pnt> polygon = tmpEntry.getValue();
                int index = tmpEntry.getKey();
                    System.out.println("()");
                    for(Pnt pnt : headerAndNormalPoints.get(index)){
                        System.out.print( " " + pnt.getIndex());
                    }
                    System.out.println();
                for(Pnt temp : polygon){
                System.out.println("(" + temp.getCoordinates()[0] + "," + temp.getCoordinates()[1] + ")");
            
                }
                System.out.println("i============== " + index);
                int[][] tempLinks = new int[vertices.length][vertices.length];
                for(int i = 0;i < vertices.length;i++) {
                    for(int j = 0;j < vertices.length;j++) {
                        tempLinks[i][j] = starLinked[i][j];
                    }
                }
                breakPointsNotInPolygons(index,index,tempLinks,polygon);
            }  
            dealWithMovedStar();
            java.util.List<Integer> indexsOfBreakPoints = new ArrayList<Integer>();

        
            reConnectPointsNotInPolygons();
        
            repaint();

            
        }
        public void breakPointsNotInPolygons(int j,int index,int[][] links,java.util.List<Pnt> polygon){
            System.out.println("break1:" + index);
            for(int i = 0;i < vertices.length;i++) {
                if(links[index][i] == 1) {
                    if(!isInPolygon(new Pnt(i,vertices[i][0],vertices[i][1]),polygon)){
                        System.out.println("break2:" + i);
                        links[index][i] = 0;
                       links[i][index] = 0;
                        starLinked[index][i] = 0;
                        starLinked[i][index] = 0;
                    for(Pnt pnt : headerAndNormalPoints.get(j)){
                        System.out.print( " " + pnt.getIndex());
                    }
                    System.out.println();
                        System.out.println("1headerAndNormalPoints.get(index)  " + headerAndNormalPoints.get(j).size());
                        headerAndNormalPoints.get(j).remove(new Pnt(i,0,0));
                        pointNotInNetwork.add(i);
                    for(Pnt pnt : headerAndNormalPoints.get(j)){
                        System.out.print( " " + pnt.getIndex());
                    }
                    System.out.println();
                        System.out.println("2headerAndNormalPoints.get(index)  " + headerAndNormalPoints.get(j).size());
                        breakAllPointsOfPreCeng(j,i,links);
                    }else {
                        links[index][i] = 0;
                        links[i][index] = 0;
                        //pointNotInNetwork.add(i);
                        breakPointsNotInPolygons(j,i,links,polygon);
                    }
                }
            }
        }
        

        public void breakAllPointsOfPreCeng(int j,int index,int[][] links){
            for(int i = 0;i < vertices.length;i++) {
                if(links[index][i] == 1) {
                        System.out.println("break3:" + i);
                    links[index][i] = 0;
                    links[i][index] = 0;
                    starLinked[index][i] = 0;
                    starLinked[i][index] = 0;

                    System.out.println("3headerAndNormalPoints.get(index)  " + headerAndNormalPoints.get(j).size());
                    for(Pnt pnt : headerAndNormalPoints.get(j)){
                        System.out.print( " " + pnt.getIndex());
                    }
                    System.out.println();
                    headerAndNormalPoints.get(j).remove(new Pnt(i,0,0));
                    for(Pnt pnt : headerAndNormalPoints.get(j)){
                        System.out.print( " " + pnt.getIndex());
                    }
                    System.out.println();
                    pointNotInNetwork.add(i);
                    System.out.println("4headerAndNormalPoints.get(index)  " + headerAndNormalPoints.get(j).size());
                    breakAllPointsOfPreCeng(j,i,links);
                }
            }
        }

        public void reConnectPointsNotInPolygons(){
        
        }
        public void movePointsbak(){
            for(int i = 0;i < vertices.length;i++){
                randomMoveOrdinaryPoint(i);
                randomMoveHeader(i);
            }
            for(int i = 0;i < vertices.length;i++){
                //if(!clusterHeaders.contains(i)) {
                for(int j = 0;j < vertices.length;j++){
                    if(linked[i][j] == 1 && (lengthOfLine(i,j,vertices) > D0)){
                        linked[i][j] = 0;
                        linked[j][i] = 0;
                    } 
                } 
                //}
            }

            trueNodelist = new ArrayList<>();
            Pnt maxVertice1 = new Pnt(0,-1950,-500);
            Pnt maxVertice2 = new Pnt(0,3050,-500);
            Pnt maxVertice3 = new Pnt(0,50,3050);
            Triangle tri =
                new Triangle(maxVertice1,maxVertice2,maxVertice3);
            Triangulation dt = new Triangulation(tri);
            for(int i : clusterHeaders){
                dt.delaunayPlace(new Pnt(i,vertices[i][0],vertices[i][1]));
            }
            Triangle.moreInfo = true;

            Set<Triangle> temp = dt.triGraph.nodeSet();

            for(Triangle s : temp) {
                if(!(s.contains(maxVertice1) || s.contains(maxVertice2) || s.contains(maxVertice3)))  trueNodelist.add(s);
            }
            linkedHeaders = null;

            linkedHeaders = new int[vertices.length][vertices.length];
            for(Triangle triTemp : trueNodelist){
                int Point_1_index = triTemp.get(0).getIndex();
                int Point_2_index = triTemp.get(1).getIndex();
                int Point_3_index = triTemp.get(2).getIndex();

                linkedHeaders[Point_1_index][Point_2_index] = 1;
                linkedHeaders[Point_2_index][Point_1_index] = 1;
                linkedHeaders[Point_2_index][Point_3_index] = 1;
                linkedHeaders[Point_3_index][Point_2_index] = 1;
                linkedHeaders[Point_3_index][Point_1_index] = 1;
                linkedHeaders[Point_1_index][Point_3_index] = 1;
            }
            for(int i = 0;i < vertices.length;i++){
                int tmp = 0;
                for(int j = 0;j < vertices.length;j++){
                    if(linkedHeaders[i][j] == 1) tmp++;
                }
                if(tmp > 0) {
                    Pnt currentheader = new Pnt(i,vertices[i][0],vertices[i][1]); 

                    java.util.List<Map.Entry<Double,Triangle>> headerlist = getTrianglesOfPoints(currentheader,trueNodelist);
                    headerMap.put(i,headerlist);
                }

            }
            for(int i = 0;i < vertices.length;i++){
                //pointOrClusterNotInNetwork 
                isConnectedToHeader(i);
                verticesInCluster.clear();
                //verticesInCluster.add(2);
                //System.out.println();
            }

            HashSet<Integer> verticesNotInNetwork = new HashSet<Integer>();
            for(HashSet<Integer> cluster : pointOrClusterNotInNetwork){
                for(int i : cluster){
                    verticesNotInNetwork.add(i);
                    System.out.printf("%3d",i);
                } 
                //System.out.println();
                System.out.println("===============");
            }

            Integer[] temp1 = verticesNotInNetwork.toArray(new Integer[]{});
            int[] freeVertices = new int[verticesNotInNetwork.size()];
            for(int q = 0;q < temp1.length;q++){
                freeVertices[q] = temp1[q].intValue(); 
                //System.out.println("freeVertices: " + freeVertices[q]);
            }
            //int[] freeVertices = (int[]) temp1;
            for(int kk : freeVertices){

                System.out.println("freeVertices: " + kk);
            }
            int p = 0;
            while(verticesNotInNetwork.size() != 0){
                int i = freeVertices[p];
                System.out.printf("i: ");
                System.out.printf(" " + i);
                System.out.println();
                if(isConnectedToHeader(i)){
                    System.out.println("enter");
                    verticesNotInNetwork.remove(i);
                    p++;
                    System.out.println("p: " + verticesNotInNetwork.size());
                    System.out.println("size : " + verticesNotInNetwork.size());
                    continue;
                }
                if(!getLine(i,verticesNotInNetwork)){

                    p++; 
                    verticesNotInNetwork.remove(i);
                }
                System.out.println("before");
                if(isConnectedToHeader(i)){
                    System.out.println("enter");
                    verticesNotInNetwork.remove(i);
                    p++;
                }
                System.out.println("p: " + verticesNotInNetwork.size());
                System.out.println("size : " + verticesNotInNetwork.size());
                System.out.println();
                System.out.println();
                System.out.println();
            }

            repaint();

        }
        public boolean isInRectangle(Pnt p){
            double x = p.getCoordinates()[0];
            double y = p.getCoordinates()[1];

            if(x <= 1050 && x >= 50 && y <= 1050&& y >= 50) return true;
            else return false;

        }
        public Pnt getIntersectPoint(Pnt c1,Pnt c2,Pnt h1,Pnt h2){
            double x1 = c1.getCoordinates()[0];
            double y1 = c1.getCoordinates()[1];
            double x2 = c2.getCoordinates()[0];
            double y2 = c2.getCoordinates()[1];

            double x3 = h1.getCoordinates()[0];
            double y3 = h1.getCoordinates()[1];
            double x4 = h2.getCoordinates()[0];
            double y4 = h2.getCoordinates()[1];
            System.out.println("getINtersectPoint");
            System.out.println("[" + x1 + "," + y1 + "]" );
            System.out.println("[" + x2 + "," + y2 + "]" );
            double x = ((x1 - x2)*(x3 * y4 - x4 * y3) - (x3 - x4) * (x1 * y2 - x2 * y1)) / ((x3 - x4) * (y1 - y2) - (x1 - x2) * (y3 - y4));
            double y = ((y1 - y2)*(x3 * y4 - x4 * y3) - (y3 - y4) * (x1 * y2 - x2 * y1)) / ((x3 - x4) * (y1 - y2) - (x1 - x2) * (y3 - y4));
            System.out.println("[" + x + "," + y + "]" );
            Pnt intersectPoint = new Pnt(0,x,y);
            System.out.println("==========");
            if((((x - x1) * (x - x2)) <= 0) && (((y - y1) * (y - y2)) <= 0) && ((((x - x3) * (x - x4)) <= 0 && (y3 == y4)) || (((y - y3) * (y - y4)) <= 0 && (x3 == x4)))) return intersectPoint;
            //if((((x - x3) * (x - x4)) <= 0) && (((y - y3) * (y - y4)) <= 0)) return intersectPoint;
            //if((((x - x1) * (x - x2)) <= 0) && (((y - y1) * (y - y2)) <= 0)) return intersectPoint;
            else return null;
        }
        public boolean getLine(int i,HashSet<Integer> settemp){
            boolean flag = false; 
            ArrayList<Integer> cluster = new ArrayList<Integer>();
            for(int j = 0;j < vertices.length;j++) {

                if(i != j) {
                    double d = lengthOfLine(i,j,vertices);

                    if(d < D0) {
                        cluster.add(j);
                    }
                }
            }
            TreeMap<Double,Integer> sortOfCluster = new TreeMap<Double,Integer>(); 
            for(int l = 0;l < cluster.size();l++){
                sortOfCluster.put(argument_n[cluster.get(l)] * argument_e[cluster.get(l)] / lengthOfLine(i,cluster.get(l),vertices),cluster.get(l));
            }
            java.util.List<Map.Entry<Double,Integer>> list = new ArrayList<Map.Entry<Double,Integer>>(sortOfCluster.entrySet());
            Collections.reverse(list);
            int numberOfConnected = 0;
            int oraginalListsize = list.size();
            for(int k = 0;k < oraginalListsize;k++){
                if(linked[list.get(0).getValue()][i] == 1) break;
                linked[i][list.get(0).getValue()] = 1;
                //linked[list.get(0).getValue()][i] = 1;
                numberOfConnected = numberOfConnectedToVertice(list.get(0).getValue(),linked);
                verticesInCluster.clear();
                if(numberOfConnected > C || settemp.contains(list.get(0).getValue())) {
                    linked[i][list.get(0).getValue()] = 0;
                    //linked[list.get(0).getValue()][i] = 0;
                    list.remove(0);
                    continue;
                }else {
                    //如果建立连接则为1
                    linked[i][list.get(0).getValue()] = 1;
                    //linked[list.get(0).getValue()][i] = 1;
                    if(argument_n[i] > 1) argument_n[i]--;
                    if(argument_n[list.get(0).getValue()] > 0) argument_n[list.get(0).getValue()]--;
                    flag = true;
                    break;
                }
            }
            return flag;
        }

        public boolean isConnectedToHeader(int i){
            int numberOfVertices = numberOfConnectedToVertice(i,linked); 
            for(int j : verticesInCluster){
                System.out.printf("vertices: %4d",j);
            } 
            System.out.println("numberOfvertices: " + numberOfVertices );
            boolean flag = true;
            flag = isConnectedVerticesCotainsHeader(verticesInCluster,clusterHeaders);
            if(!flag){
                HashSet<Integer> temp = new HashSet<Integer>();
                for(int j : verticesInCluster){

                    temp.add(j); 
                }
                pointOrClusterNotInNetwork.add(temp);    
            }
            return flag;   
        }

        public void randomMoveOrdinaryPoint(int i){
            
            vertices[i][0] = vertices[i][0] + (int)(getMoveX(S1));
            vertices[i][1] = vertices[i][1] + (int)(getMoveY(S1));
            if(vertices[i][0] < 50) {
                vertices[i][0] = 50 + 50 - vertices[i][0];
            } else if(vertices[i][0] > 1050) {
                vertices[i][0] = 1050 - (vertices[i][0] - 1050);
            }
            if(vertices[i][1] < 50) {
                vertices[i][1] = 50 + 50 - vertices[i][1];
            } else if(vertices[i][1] > 1050) {
                vertices[i][1] = 1050 - (vertices[i][1] - 1050);
            }
        }

        public void randomMoveHeader(int i){
            vertices[i][0] = vertices[i][0] + (int)getMoveX(S2);
            vertices[i][1] = vertices[i][1] + (int)getMoveY(S2);
            if(vertices[i][0] < 50) {
                vertices[i][0] = 50 + 50 - vertices[i][0];
            } else if(vertices[i][0] > 1050) {
                vertices[i][0] = 1050 - (vertices[i][0] - 1050);
            }
            if(vertices[i][1] < 50) {
                vertices[i][1] = 50 + 50 - vertices[i][1];
            } else if(vertices[i][1] > 1050) {
                vertices[i][1] = 1050 - (vertices[i][1] - 1050);
            }
            System.out.println("randomMoveHeader " + i + ": " + "(" + vertices[i][0] + "," + vertices[i][1] + ")");
        }

        public double getRandomRadian(){
            return 360 * rnd.nextDouble(); 
        }

        public double getMoveX(int speed){
            return speed * TIMETOMOVE * Math.cos(getRandomRadian());
        }

        public double getMoveY(int speed){
            return speed * TIMETOMOVE * Math.sin(getRandomRadian());
        }

        public void printrec(){
            for(int i = 0;i < vertices.length;i++){
                for(int j = 0;j < vertices.length;j++){
                    System.out.printf("%1d",linked[i][j]);
                }
                System.out.printf("\n"); 
                //System.out.printf("\n"); 
            } 

            System.out.println("======================================================================"); 
            System.out.printf("\n"); 
            for(int i = 0;i < vertices.length;i++){
                for(int j = 0;j < vertices.length;j++){
                    System.out.printf("%1d",linkedHeaders[i][j]);
                }
                System.out.printf("\n"); 
                //System.out.printf("\n"); 
            } 
        }
        }

        public class Diagram extends JFrame {
            public Diagram(String arg) {
                super("Diagram -- ChangXinYu");
                Diagram_Panel dp = new Diagram_Panel();
                dp.newGraph();
                /*
                BufferedImage bi = new BufferedImage(1090,1090,BufferedImage.TYPE_INT_ARGB);
                Graphics2D g2d = bi.createGraphics();
                g2d.setColor(Color.WHITE);
                g2d.fillRect(0,0,1090,1090);
                //g2d.dispose();
                dp.paint(g2d);
                try {
                ImageIO.write(bi,"PNG",new File("Diagrams.png"));
                } catch(IOException e) {
                    e.printStackTrace();
                }
                //dp.setBackground(null);

*/

            }
            public Diagram() {
                super("Diagram -- ChangXinYu");
                setLocationRelativeTo(null);
                setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
                Diagram_Panel dp = new Diagram_Panel();
                dp.setPreferredSize(new Dimension(1500,1500));
                JScrollPane pane = new JScrollPane(dp);

                dp.setBackground(Color.WHITE);
                dp.newGraph();
                /*
                BufferedImage bi = new BufferedImage(1090,1090,BufferedImage.TYPE_INT_ARGB);
                Graphics2D g2d = bi.createGraphics();
                g2d.setColor(Color.WHITE);
                g2d.fillRect(0,0,1090,1090);
                //g2d.dispose();
                dp.paint(g2d);
                try {
                ImageIO.write(bi,"PNG",new File("Diagrams.png"));
                } catch(IOException e) {
                    e.printStackTrace();
                }
                //dp.setBackground(null);

*/
                setContentPane(pane);
                //setContentPane(dp);
                setSize(1500,1500);
                setVisible(true);

            }
            public static void main(String args[]) {
                Diagram frame = new Diagram();
            }
        }
