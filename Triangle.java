import java.util.Arrays;
import java.util.Collection;
import java.util.Iterator;
import java.util.NoSuchElementException;

/**
 * A Triangle is an immutable Set of exactly three Pnts.
 *
 * All Set operations are available. Individual vertices can be accessed via
 * iterator() and also via triangle.get(index).
 *
 * Note that, even if two triangles have the same vertex set, they are
 * *different* triangles. Methods equals() and hashCode() are consistent with
 * this rule.
 *
 * @author Paul Chew
 *
 * Created December 2007. Replaced general simplices with geometric triangle.
 *
 */
class Triangle extends ArraySet<Pnt> {

    private int idNumber;                   // The id number
    private Pnt circumcenter = null;        // The triangle's circumcenter

    private static int idGenerator = 0;     // Used to create id numbers
    public static boolean moreInfo = false; // True iff more info in toString

    public boolean isEdgeTriangle = false;

    /**
     * @param vertices the vertices of the Triangle.
     * @throws IllegalArgumentException if there are not three distinct vertices
     */
    public Triangle (Pnt... vertices) {
        this(Arrays.asList(vertices));
    }

    /**
     * @param collection a Collection holding the Simplex vertices
     * @throws IllegalArgumentException if there are not three distinct vertices
     */
    public Triangle (Collection<? extends Pnt> collection) {
        super(collection);
        idNumber = idGenerator++;
        if (this.size() != 3)
            throw new IllegalArgumentException("Triangle must have 3 vertices");
    }
    public int getIdNumber(){
        return idNumber;
    }
    @Override
    public String toString () {
        if (!moreInfo) return "Triangle" + idNumber;
        return "Triangle" + idNumber + super.toString();
    }

    /**
     * Get arbitrary vertex of this triangle, but not any of the bad vertices.
     * @param badVertices one or more bad vertices
     * @return a vertex of this triangle, but not one of the bad vertices
     * @throws NoSuchElementException if no vertex found
     */
    public Pnt getVertexButNot (Pnt... badVertices) {
        Collection<Pnt> bad = Arrays.asList(badVertices);
        for (Pnt v: this) if (!bad.contains(v)) return v;
        throw new NoSuchElementException("No vertex found");
    }

    /**
     * True iff triangles are neighbors. Two triangles are neighbors if they
     * share a facet.
     * @param triangle the other Triangle
     * @return true iff this Triangle is a neighbor of triangle
     */
    public boolean isNeighbor (Triangle triangle) {
        int count = 0;
        for (Pnt vertex: this)
            if (!triangle.contains(vertex)) count++;
        return count == 1;
    }

    /**
     * Report the facet opposite vertex.
     * @param vertex a vertex of this Triangle
     * @return the facet opposite vertex
     * @throws IllegalArgumentException if the vertex is not in triangle
     */
    public ArraySet<Pnt> facetOpposite (Pnt vertex) {
        ArraySet<Pnt> facet = new ArraySet<Pnt>(this);
        if (!facet.remove(vertex))
            throw new IllegalArgumentException("Vertex not in triangle");
        return facet;
    }

    /**
     * @return the triangle's circumcenter
     */
    public Pnt getCircumcenter () {
        if (circumcenter == null)
            circumcenter = Pnt.circumcenter(this.toArray(new Pnt[0]));
        return circumcenter;
    }
    /**
     * @return if Triangle is edge triangle ,return the point 
     */
    public Pnt getPointInEdge (int indexOfEdgePoint,int indexOfCenter, Pnt point1, Pnt point2, Pnt point3, Pnt point4) {
                Pnt pFrom,pTo,pointOnRectangle,pLeft,pRight,center;
                Pnt[] pntArray= this.toArray(new Pnt[0]); 
                pLeft = new Pnt();
                pRight = new Pnt();
                center = new Pnt();
                System.out.println("indexOfEdgePoint ====" + indexOfEdgePoint);
                System.out.println("indexOfCenter ====" + indexOfCenter);
                for(Pnt tmp : pntArray){
                    if(tmp.getIndex() == indexOfEdgePoint) pLeft = tmp;
                    else if(tmp.getIndex() == indexOfCenter) center = pRight = tmp; 
                }
                pFrom = this.getCircumcenter();
                pTo = new Pnt(0,(pLeft.getCoordinates()[0] + pRight.getCoordinates()[0]) / 2,(pLeft.getCoordinates()[1] + pRight.getCoordinates()[1]) / 2);
                Pnt p1 = getIntersectPoint(pFrom,pTo,point1,point2,center);
                Pnt p2 = getIntersectPoint(pFrom,pTo,point2,point3,center);
                Pnt p3 = getIntersectPoint(pFrom,pTo,point3,point4,center);
                Pnt p4 = getIntersectPoint(pFrom,pTo,point4,point1,center);
                
                            if(p1 != null && (p1.getCoordinates()[0] >= 50 && p1.getCoordinates()[0] <= 1050))
                                pointOnRectangle = p1;
                            else if(p2 != null && (p2.getCoordinates()[1] >= 50 && p2.getCoordinates()[1] <= 1050))
                            //else if(getIntersectPoint(pFrom,pTo,point2,point3,center) != null) 
                                pointOnRectangle = p2;
                            else if(p3 != null && (p3.getCoordinates()[0] >= 50 && p3.getCoordinates()[0] <= 1050))
                            //else if(getIntersectPoint(pFrom,pTo,point3,point4,center) != null) 
                                pointOnRectangle = getIntersectPoint(pFrom,pTo,point3,point4,center);
                            else 
                                pointOnRectangle = getIntersectPoint(pFrom,pTo,point4,point1,center);
        
        return pointOnRectangle;
    }

    public Pnt getIntersectPoint(Pnt c1,Pnt c2,Pnt h1,Pnt h2,Pnt center){
        Pnt point1 = new Pnt(0,50,50);
        Pnt point2 = new Pnt(0,1050,50);
        Pnt point3 = new Pnt(0,1050,1050);
        Pnt point4 = new Pnt(0,50,1050);
        double x1 = c1.getCoordinates()[0];
        double y1 = c1.getCoordinates()[1];
        double x2 = c2.getCoordinates()[0];
        double y2 = c2.getCoordinates()[1];

        double x3 = h1.getCoordinates()[0];
        double y3 = h1.getCoordinates()[1];
        double x4 = h2.getCoordinates()[0];
        double y4 = h2.getCoordinates()[1];

        double x5 = center.getCoordinates()[0];
        double y5 = center.getCoordinates()[1];

        System.out.println("getINtersectPoint");
        System.out.println("[" + x1 + "," + y1 + "]" );
        System.out.println("[" + x2 + "," + y2 + "]" );
        double x = ((x1 - x2)*(x3 * y4 - x4 * y3) - (x3 - x4) * (x1 * y2 - x2 * y1)) / ((x3 - x4) * (y1 - y2) - (x1 - x2) * (y3 - y4));
        double y = ((y1 - y2)*(x3 * y4 - x4 * y3) - (y3 - y4) * (x1 * y2 - x2 * y1)) / ((x3 - x4) * (y1 - y2) - (x1 - x2) * (y3 - y4));
        System.out.println("[" + x + "," + y + "]" );
        Pnt intersectPoint = new Pnt(0,x,y);
        System.out.println("==========");
        double width = Math.abs(x - x5);
        double height = Math.abs(y - y5);
        if((x3 - x4) == 0) {
            //if((((y - y3) * (y - y4)) <= 0) && (Math.abs(y - y5) > Math.abs(y - y2))) return intersectPoint;
            if((((y - y3) * (y - y4)) <= 0) && (Math.sqrt(width * width + height * height) < 500)) return intersectPoint;
            else return null;
        }else {
            //if((((x - x3) * (x - x4)) <= 0) && (Math.abs(x - x5) > Math.abs(x - x2))) return intersectPoint;
            if((((x - x3) * (x - x4)) <= 0) && (Math.sqrt(width * width + height * height) < 500)) return intersectPoint;
            else return null;
        }
    }

    /* The following two methods ensure that a Triangle is immutable */

    @Override
    public boolean add (Pnt vertex) {
        throw new UnsupportedOperationException();
    }

    @Override
    public Iterator<Pnt> iterator () {
        return new Iterator<Pnt>() {
            private Iterator<Pnt> it = Triangle.super.iterator();
            public boolean hasNext() {return it.hasNext();}
            public Pnt next() {return it.next();}
            public void remove() {throw new UnsupportedOperationException();}
        };
    }

    /* The following two methods ensure that all triangles are different. */

    @Override
    public int hashCode () {
        return idNumber^(idNumber>>>32);
    }

    @Override
    public boolean equals (Object o) {
        return (this == o);
    }

}
