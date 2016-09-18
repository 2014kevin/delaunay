import Jama.EigenvalueDecomposition;
import Jama.Matrix;

import java.io.*;
import java.util.*;
public class Connectivity {
    private static int arg;

    public Connectivity(int arg){
        this.arg = arg;
    }
    public static void main(String[] args) {
    
 double [][] tree={  
                {-1,6,1,5,-1,-1},  
                {6,-1,5,-1,3,-1},  
                {1,5,-1,5,6,4},  
                {5,-1,5,-1,-1,2},  
                {-1,3,6,-1,-1,6},  
                {-1,-1,4,2,6,-1}              
        };  
 double [][] tree1={  
     {1,-2,2},
     {-2,-2,4},
     {2,4,-2}
        };  
        double[][] GetEV=new double[3][2];  
        EigenValue(tree1,3,1000,1,GetEV);  

        String Strr=new String("");  
        for(int i=0;i<3;i++)  
        {  
            String str=String.valueOf(GetEV[i][0]);//  
            Strr+=str;  
            Strr+=" ";  
            String str1=String.valueOf(GetEV[i][1]);  
            Strr+=str1;  
            Strr+="\n";  
          
        }  
          
          
       System.out.println("Strr\n" + Strr);  
        //PRIM(tree, 0, 6);  
        /*
        double[][] TestMatrix = {  
                   {1, 22, 34,22},   
      
                   {1, 11,5,21} ,  
                   {0,1,5,11},  
                   {7,2,13,19}};      
        double[][] TMatrix1={  
                {1,2,3},{2,1,1},{2,2,2}   
        };  
        double[][]TMatrix2={  
                {1,2},{2,3}   
        };  
        double[][] GetEV=new double[3][2];  
        EigenValue(TMatrix2,2,400,4,GetEV);  
        String Strr=new String("");  
        for(int i=0;i<2;i++)  
        {  
            String str=String.valueOf(GetEV[i][0]);//  
            Strr+=str;  
            Strr+=" ";  
            String str1=String.valueOf(GetEV[i][1]);  
            Strr+=str1;  
            Strr+="\n";  
          
        }  
          
          
       System.out.println("Strr" + Strr);  
       */
    }

    public static void getEigenvalue(double[][] MatrixThis, int index) {
        for(int i = 0; i < index;i++){
            System.out.println("tree[0]" + "["+ i + "]:" + MatrixThis[0][i]);
        }
        double[][] temp1 = new double[index][index];
        for(int i = 0; i < index;i++){
            
            for(int j = 0; j < index; j++){
                temp1[i][j] = MatrixThis[i][j];
            }
        }
        getMSTMatrix(temp1, index);
        getNormalMatrix(MatrixThis,index);
    }

    public static void getNormalMatrix(double[][] MatrixThis, int index){

            System.out.println("temp1 ");
        for(int i = 0; i < index; i++) {
            for(int j = 0; j < index; j++){
            System.out.printf("%3d",(int)MatrixThis[i][j]);
            }
            System.out.println();
        }
        int[][] degreesMatrix = new int[index][index];
        double[][] tempMatrix = new double[index][index];
        double[][] GetEV=new double[index][2];
        double[] realEigenvalues = new double[index];
        int degree = 0;
        for(int i = 0; i < index;i++){
            
            for(int j = 0; j < index; j++){
                if(MatrixThis[i][j] != -1) {
                    degree++;
                    MatrixThis[i][j] = 1;
                }
                else MatrixThis[i][j] = 0;
                if(i != j) degreesMatrix[i][j] = 0;
            }
            degreesMatrix[i][i] = degree;
            degree = 0;
        }
        for(int i = 0; i < index;i++){
            for(int j = 0; j < index; j++){
                tempMatrix[i][j] = degreesMatrix[i][j] - MatrixThis[i][j];
            }
        }

            System.out.println("temp1 ");
        for(int i = 0; i < index; i++) {
            for(int j = 0; j < index; j++){
            System.out.printf("%2d",(int)tempMatrix[i][j]);
            }
            System.out.println();
        }
        boolean isduichen = true;
        for(int i = 0; i < index; i++) {
            for(int j = 0; j < index; j++){
                if(tempMatrix[i][j] != tempMatrix[j][i])
                {
                    isduichen = false;
                }
            }
        }
        if(!isduichen) {System.out.println("bu shi dui zhen ju zhen");}
        else System.out.println("shi dui zhen ju zhen");
        Matrix matrix = new Matrix(tempMatrix);
        EigenvalueDecomposition eigenvalueDecomposition = new EigenvalueDecomposition(matrix);
        realEigenvalues = eigenvalueDecomposition.getRealEigenvalues();
        System.out.println(Arrays.toString(eigenvalueDecomposition.getImagEigenvalues()));
        System.out.println(Arrays.toString(eigenvalueDecomposition.getRealEigenvalues()));
        Arrays.sort(realEigenvalues);
        /*
        EigenValue(tempMatrix,index,400,4,GetEV);
        double minValue = Double.MAX_VALUE;
        List<Double> list = new ArrayList<Double>();
        for(int i = 0; i < index; i++){
            double temp = GetEV[i][0];
            System.out.println("normal" + i + ": " + GetEV[i][0] + ", " + GetEV[i][1]);
            //if(temp > 0 && temp < minValue){
            if(temp > 0){
                //minValue = temp;
                list.add(temp);
            }
        }
        Collections.sort(list);
        */
        WriteStringToFile( arg + " min Normal Value: " + String.valueOf(realEigenvalues[1]) + "\n");
        System.out.println(arg + " min Normal Value: " + realEigenvalues[1]);
    }
    public static void getMSTMatrix(double[][] MatrixThis, int index){

        int[][] mins = PRIM(MatrixThis,0,index);
        for(int i = 0; i < index; i++) {
            System.out.println("mins " + i + " :" + mins[i][0]);
        }
        double[][] GetEV=new double[index][2];  
        double[][] MSTMatrix = new double[index][index];
        for(int i = 1; i < index; i++) {
            MSTMatrix[mins[i][0]][i] = 1;
            MSTMatrix[i][mins[i][0]] = 1;
        }
        int[][] degreesMatrix = new int[index][index];
        double[][] tempMatrix = new double[index][index];
        double[] realEigenvalues = new double[index];

        int degree = 0;
        for(int i = 0; i < index;i++){
            
            for(int j = 0; j < index; j++){
                if(MSTMatrix[i][j] == 1.0) degree++;
                else MSTMatrix[i][j] = 0;
            }
            degreesMatrix[i][i] = degree;
            degree = 0;
        }
            System.out.println("degrees ");
        for(int i = 0; i < index; i++) {
            for(int j = 0; j < index; j++){
            System.out.print(" " + degreesMatrix[i][j]);
            }
            System.out.println();
        }
        for(int i = 0; i < index;i++){
            for(int j = 0; j < index; j++){
                tempMatrix[i][j] = degreesMatrix[i][j] - MSTMatrix[i][j];
            }
        }
            System.out.println("temp ");
        for(int i = 0; i < index; i++) {
            for(int j = 0; j < index; j++){
            System.out.print("%2d" + (int)tempMatrix[i][j]);
            }
            System.out.println();
        }
        Matrix matrix = new Matrix(tempMatrix);
        EigenvalueDecomposition eigenvalueDecomposition = new EigenvalueDecomposition(matrix);
        realEigenvalues = eigenvalueDecomposition.getRealEigenvalues();
        System.out.println(Arrays.toString(eigenvalueDecomposition.getImagEigenvalues()));
        System.out.println(Arrays.toString(eigenvalueDecomposition.getRealEigenvalues()));
        Arrays.sort(realEigenvalues);
        /*
        EigenValue(tempMatrix,index,400,4,GetEV);


        double minValue = Double.MAX_VALUE;
        List<Double> list = new ArrayList<Double>();
        for(int i = 0; i < index; i++){
            double temp = GetEV[i][0];
            //System.out.println("mst" + i + ": " + GetEV[i][0]);
            System.out.println("normal" + i + ": " + GetEV[i][0] + ", " + GetEV[i][1]);
            //if(temp > 0 && temp < minValue){
            if(temp > 0){
                //minValue = temp;
                list.add(temp);
            }
        }
        Collections.sort(list);*/
        WriteStringToFile(arg + " min MST Value: " + String.valueOf(realEigenvalues[1]) + "\n");
        System.out.println( arg + " min MST Value: " + realEigenvalues[1]);
        /*
        for(int i = 0; i < index;i++){
            for(int j = 0; j < index; j++){
                if(MatrixThis[i][j] == 1) {
                    tree[i][j] = lengthOfLine(i,j,vertices);
                    
                }else {
                    tree[i][j] = -1;
                }
            }
        }
        for(int i = 0; i < index;i++){
            System.out.println("tree[0][i]" + tree[0][i]);
        }
        */

        //PRIM(tree,0,index);
/*
        int numOfEdge = 0;
        for(int i = 0; i < index;i++){
            for(int j = 0; j < index; j++){
                numOfEdge++;
            }
        }
        int[][] edges = new int[numOfEdge][3];
        int row = 0;
        for(int i = 0; i < index;i++){
            for(int j = 0; j < index; j++){
                if(MatrixThis[i][j] == 1) {
                    edges[row][0] = i;
                    edges[row][1] = j;
                    edges[row][2] = lengthOfLine(i,j,vertices);
                }
            }
        }

        WeightedGraph<String> graph = new WeightedGraph<String>(edges, index);
        WeightedGraph<String>.MST tree = graph.getMinimumSpanningTree();
        System.out.println("printTree");
        tree.printTree();
 */       
    }
    public static int[][] PRIM(double [][] graph,int start,int n){  
        int [][] mins=new int [n][2];//用于保存集合U到V-U之间的最小边和它的值，mins[i][0]值表示到该节点i边的起始节点  
                                     //值为-1表示没有到它的起始点，mins[i][1]值表示到该边的最小值，  
                                     //mins[i][1]=0表示该节点已将在集合U中  
        for(int i=0;i<n;i++){//初始化mins  
          
            if(i==start){  
                mins[i][0]=-1;  
                mins[i][1]=0;  
            }else if( graph[start][i]!=-1){//说明存在（start，i）的边  
                mins[i][0]=start;  
                mins[i][1]= (int)graph[start][i];  
            }else{  
                mins[i][0]=-1;  
                mins[i][1]=Integer.MAX_VALUE;  
            }  
//          System.out.println("mins["+i+"][0]="+mins[i][0]+"||mins["+i+"][1]="+mins[i][1]);  
        }  
        for(int i=0;i<n-1;i++){  
            int minV=-1,minW=Integer.MAX_VALUE;  
            for(int j=0;j<n;j++){//找到mins中最小值，使用O(n^2)时间  
                  
                if(mins[j][1]!=0&&minW>mins[j][1]){  
                    minW=mins[j][1];  
                    minV=j;  
                }  
            }  
//          System.out.println("minV="+minV);  
            mins[minV][1]=0;  
            System.out.println("最小生成树的第"+i+"条最小边=<"+(mins[minV][0]+1)+","+(minV+1)+">，权重="+minW);  
            for(int j=0;j<n;j++){//更新mins数组  
                if(mins[j][1]!=0){  
//                  System.out.println("MINV="+minV+"||tree[minV][j]="+tree[minV][j]);  
                    if( graph[minV][j]!=-1&& graph[minV][j]<mins[j][1]){  
                        mins[j][0]=minV;  
                        mins[j][1]= (int)graph[minV][j];  
                    }  
                }  
            }  
        }  
        return mins;
    }  
    public static int lengthOfLine(int i, int j, int[][] vertices) {
        double width = Math.abs(vertices[i][0] - vertices[j][0]);
        double height = Math.abs(vertices[i][1] - vertices[j][1]);
        return (int)Math.sqrt(width * width + height * height);
    }

    public static int Hessenberg(double[][] MatrixThis,int n,double[][]ret)
    {  
      
        int i;  
        int j;  
        int k;  
        double temp;  
        int MaxNu;  
        n-=1;  
        for(k=1;k<=n-1;k++)  
        {  
            i=k-1;  
            MaxNu=k;  
            temp=Math.abs(MatrixThis[k][i]);
            for(j=k+1;j<=n;j++)  
            {  
                if(Math.abs(MatrixThis[j][i])>temp)
                {  
                    MaxNu=j;  
                }  
            }  
            ret[0][0]=MatrixThis[MaxNu][i];
            i=MaxNu;  
            if(ret[0][0]!=0)  
            {  
                if(i!=k)  
                {  
                    for(j=k-1;j<=n;j++)  
                    {  
                        temp=MatrixThis[i][j];
                        MatrixThis[i][j]=MatrixThis[k][j];
                        MatrixThis[k][j]=temp;
                    }  
                    for(j=0;j<=n;j++)  
                    {  
                        temp=MatrixThis[j][i];
                        MatrixThis[j][i]=MatrixThis[j][k];
                        MatrixThis[j][k]=temp;
                    }  
                }  
                for(i=k+1;i<=n;i++)  
                {  
                    temp=MatrixThis[i][k-1]/ret[0][0];
                    MatrixThis[i][k-1]=0;
                    for(j=k;j<=n;j++)  
                    {  
                        MatrixThis[i][j]-=temp*MatrixThis[k][j];
                    }  
                    for(j=0;j<=n;j++)  
                    {  
                        MatrixThis[j][k]+=temp*MatrixThis[j][i];
                    }  
                }  
            }  
        }  
        for(i=0;i<=n;i++)  
        {  
            for(j=0;j<=n;j++)  
            {  
                ret[i][j]=MatrixThis[i][j];
            }  
        }  
        return n+1;  
    }  
    ////////////  
    public static boolean EigenValue(double[][]MatrixThis,int n,int LoopNu,int Erro,double[][]Ret)
    {  
        int i=MatrixThis.length;
        if(i!=n)  
        {  
            return false;  
        }  
        int j;  
        int k;  
        int t;  
        int m;  
        double[][]A=new double[n][n];  
        double erro=Math.pow(0.1, Erro);  
        double b;  
        double c;  
        double d;  
        double g;  
        double xy;  
        double p;  
        double q;  
        double r;  
        double x;  
        double s;  
        double e;  
        double f;  
        double z;  
        double y;  
        int loop1=LoopNu;  
        Hessenberg(MatrixThis,n,A);//将方阵K1转化成上Hessenberg矩阵A
        m=n;  
        while(m!=0)  
        {  
            t=m-1;  
            while(t>0)  
            {  
                if(Math.abs(A[t][t-1])>erro*(Math.abs(A[t-1][t-1])+Math.abs(A[t][t])))  
                {  
                    t-=1;  
                }  
                else  
                {  
                    break;  
                }  
            }  
            if(t==m-1)  
            {  
                Ret[m-1][0]=A[m-1][m-1];  
                Ret[m-1][1]=0;  
                m-=1;  
                loop1=LoopNu;  
                  
            }  
            else if(t==m-2)  
            {  
                b=-(A[m-1][m-1]+A[m-2][m-2]);  
                c=A[m-1][m-1]*A[m-2][m-2]-A[m-1][m-2]*A[m-2][m-1];  
                d=b*b-4*c;  
                y=Math.pow(Math.abs(d), 0.5);  
                if(d>0)  
                {  
                    xy=1;  
                    if(b<0)  
                    {  
                        xy=-1;  
                    }  
                    Ret[m-1][0]=-(b+xy*y)/2;  
                    Ret[m-1][1]=0;  
                    Ret[m-2][0]=c/Ret[m-1][0];  
                    Ret[m-2][1]=0;  
                }  
                else  
                {  
                    Ret[m-1][0]=-b/2;  
                    Ret[m-2][0]=Ret[m-1][0];  
                    Ret[m-1][1]=y/2;  
                    Ret[m-2][1]=-Ret[m-1][1];  
                }  
                m-=2;  
                loop1=LoopNu;  
            }  
            else  
            {  
                if(loop1<1)  
                {  
                    return false;  
                }  
                loop1-=1;  
                j=t+2;  
                while(j<m)  
                {  
                    A[j][j-2]=0;  
                    j+=1;  
                }  
                j=t+3;  
                while(j<m)  
                {  
                    A[j][j-3]=0;  
                    j+=1;  
                }  
                k=t;  
                while(k<m-1)  
                {  
                    if(k!=t)  
                    {  
                        p=A[k][k-1];  
                        q=A[k+1][k-1];  
                        if(k!=m-2)  
                        {  
                            r=A[k+2][k-1];  
                        }  
                        else  
                        {  
                            r=0;  
                        }  
                    }  
                    else  
                    {  
                        b=A[m-1][m-1];  
                        c=A[m-2][m-2];  
                        x=b+c;  
                        y=c*b-A[m-2][m-1]*A[m-1][m-2];  
                        p=A[t][t]*(A[t][t]-x)+A[t][t+1]*A[t+1][t]+y;  
                        q=A[t+1][t]*(A[t][t]+A[t+1][t+1]-x);  
                        r=A[t+1][t]*A[t+2][t+1];  
                    }  
                    if(p!=0||q!=0||r!=0)  
                    {  
                        if(p<0)  
                        {  
                            xy=-1;  
                        }  
                        else  
                        {  
                            xy=1;  
                        }  
                        s=xy*Math.pow(p*p+q*q+r*r, 0.5);  
                        if(k!=t)  
                        {  
                            A[k][k-1]=-s;  
                        }  
                        e=-q/s;  
                        f=-r/s;  
                        x=-p/s;  
                        y=-x-f*r/(p+s);  
                        g=e*r/(p+s);  
                        z=-x-e*q/(p+s);  
                        for(j=k;j<=m-1;j++)  
                        {  
                            b=A[k][j];  
                            c=A[k+1][j];  
                            p=x*b+e*c;  
                            q=e*b+y*c;  
                            r=f*b+g*c;  
                            if(k!=m-2)  
                            {  
                                b=A[k+2][j];  
                                p+=f*b;  
                                q+=g*b;  
                                r+=z*b;  
                                A[k+2][j]=r;  
                            }  
                            A[k+1][j]=q;  
                            A[k][j]=p;  
                              
                        }  
                        j=k+3;  
                        if(j>=m-1)  
                        {  
                            j=m-1;  
                        }  
                        for(i=t;i<=j;i++)  
                        {  
                            b=A[i][k];  
                            c=A[i][k+1];  
                            p=x*b+e*c;  
                            q=e*b+y*c;  
                            r=f*b+g*c;  
                            if(k!=m-2)  
                            {  
                                b=A[i][k+2];  
                                p+=f*b;  
                                q+=g*b;  
                                r+=z*b;  
                                A[i][k+2]=r;  
                            }  
                            A[i][k+1]=q;  
                            A[i][k]=p;  
                        }  
                    }  
                    k+=1;  
                }  
                  
            }  
              
        }  
        return true;  
    }
    public void WriteStringToFile(String filePath, String str) {
        try {
            File file = new File(filePath);
            PrintStream ps = new PrintStream(new FileOutputStream(file));
            ps.println("Record");// 往文件里写入字符串
            ps.append(str);// 在已有的基础上添加字符串
        } catch (FileNotFoundException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
    }
    public static void WriteStringToFile(String str) {
        String filePath = "D:\\IdeaWorkSpace\\abc.txt";
        try {
            FileWriter writer = new FileWriter(filePath, true);
            writer.write(str);
            writer.close();

        } catch (FileNotFoundException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        } finally {

        }
    }



}
