package GenericPID.Testing;
// package Approximations.Testing;
//import Testing.*;

import GenericPID.ApproximationUtils.Functions;
import GenericPID.Extensible.BooleanFunction;
import GenericPID.Extensible.DoubleFunction;
import GenericPID.Extensible.PairFunction;
import GenericPID.Implementations.NoJump;
import GenericPID.Pair;
import GenericPID.Debug;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Graphics;
import java.awt.Point;
import javax.swing.SwingUtilities;


import javax.swing.JFrame;
import javax.swing.JPanel;
import java.lang.Math;

import java.util.ArrayList;


/**
 * A 2d Cartesian Graph object that drives graphics for a viewer. Use init() to render
 * and drawagain() to render again. Use addPlot or addPoint to add info, and the colors are from
 * the awt library. The graph has multiple plots, and will auto adjust y window, but the info can be changed
 * with the associated GraphConfig.
 * @param Jump : For many functions, a <b><i>true</i></b> jump will skip the connection before  that point.
 */
public class Graph extends JPanel {

    private static Debug Debug;
    private static final boolean debug = Debug.debug_Graph;
    private static final boolean debug2 = Debug.debug_Graph2;

    private GraphConfig config;
    private ArrayList<ArrayList<Pair>> graph;
    private ArrayList<ArrayList<Boolean>> jumps;

    private JFrame frame;
    private ArrayList<Color> colors;
    private int pxw;
    private int pxh;

    public static void main(String[] args) {
        test2();
    }
    public static void test1() {
        GraphConfig conf = new Graph.GraphConfig();
        Graph example = new Graph(conf);
        double dt = 0.1;
        example.addPlot(Color.RED);
        for(double t = -3*3.14159; t < 3*3.14159; t+=dt) {
            example.addPoint(t, Math.sin(t), false, 0);
        }
        
        example.addPlot(Color.BLUE);
        example.plot(-3*3.1415, 3* 3.1415,
        new DoubleFunction() {
            public double eval(double x) {
                return Math.sin(x) + 0.1;
            }
        }, new NoJump()
        /*
        new BooleanFunction() {
            public boolean eval(double x) {
                return (Math.abs(x) % 1 > 0.5);
            }
        }*/, dt, 0);

        example.init(1920, 1080, "Graph");
    }
    public static void test2() {
        GraphConfig conf = new Graph.GraphConfig();
        conf.x1 = -20;
        conf.x2 = 20;
        conf.y1 = -20;
        conf.y2 = 20;
        Graph example = new Graph(conf);
        double dt = 0.01;
        Color colors[] = {Color.RED, Color.PINK, Color.BLUE.darker(), Color.BLUE, Color.GREEN, Color.ORANGE, Color.YELLOW, Color.GREEN.brighter()};
        for (int i = 0; i < 7; i++) {
            example.addPlot(colors[i]);
            example.plotParamaterized(3.1415*-1, 3.1415*1,
            new PairFunction() {
                public Pair eval(double x) {
                    return new Pair(Math.sin(x)*9, Math.cos(x)*9);
                }
            }, 
            new BooleanFunction() {
                public boolean eval(double x) {
                    return (Functions.goodmod(Math.abs(x) + 0.01 * x, 1) > 0.5);
                }
            }, dt, i);
        }
        example.init(1600, 900, "yee jump!");        
    }

    public void addPlot(Color awtcolor) {
        graph.add(new ArrayList<Pair>());
        jumps.add(new ArrayList<Boolean>());
        colors.add(awtcolor);
    }

    public void addPoint(double x, double y, boolean jump, int plotNumber) {
        Pair p = new Pair(x, y);
        graph.get(plotNumber).add(p);
        jumps.get(plotNumber).add(jump);
        //if (debug2) System.out.printf("|   Adding Point   |");
        if (p.x < config.x1 && p.x - config.extendby > config.x1min) {
            backExtendBy(config.extendby);
        }
        if (p.x > config.x2 && p.x + config.extendby < config.x2max) {
            extendBy(config.extendby);
            if(debug) System.out.println("X2 extended");
        }
        if (p.y > config.y2 && p.y + config.extendby < config.y2max) {
            config.y2 = p.y + config.extendby;
            if(debug) System.out.printf("Y2 extended to %f\n", config.y2);
        }
        if (p.y < config.y1 && p.y - config.extendby > config.y1min) {
            config.y1 = p.y - config.extendby;
            if(debug) System.out.printf("Y1 extended to %f\n", config.y1);
        }
        if (ppsx() < config.minpps) {
            xscaleBy(config.scaleby);
        }
        if (ppsy() < config.minpps) {
            yscaleBy(config.scaleby);
        }
    }

    public void popLastPoint(int plotNumber) {
        graph.get(plotNumber).remove(graph.get(plotNumber).size() - 1);
        jumps.get(plotNumber).remove(jumps.get(plotNumber).size() - 1);
    }

    public void popPoint(int i, int plotNumber) {
        graph.get(plotNumber).remove(i);
        jumps.get(plotNumber).remove(i);
    }

    public Pair getPoint(int i, int plotNumber) {
        return graph.get(plotNumber).get(i);
    }

    public void plot(double x1, double x2, DoubleFunction f, BooleanFunction doJump, double dx, int plotNumber) {
        for (double x = x1; x < x2; x += dx) {
            if (debug2) System.out.printf("|          Plotting          |");
            addPoint(x, f.eval(x), doJump.eval(x), plotNumber);
        }
    }

    public void plotParamaterized(double t1, double t2, PairFunction f, BooleanFunction doJump, double dt, int plotNumber) {
        for (double t = t1; t < t2; t += dt) {
            Pair p = f.eval(t);
            addPoint(p.x, p.y, doJump.eval(t), plotNumber);
        }
    }

    public void plot(double x1, double x2, DoubleFunction f, double dx, int plotNumber) {
        plot(x1, x2, f, new NoJump(), dx, plotNumber);
    }

    public void plotParamaterized(double t1, double t2, PairFunction f, double dt, int plotNumber) {
        plotParamaterized(t1, t2, f, new NoJump(), dt, plotNumber);
    }

    public void extendBy(double Dx) {
        config.x2 += Dx;
    }
    public void backExtendBy(double Dx) {
        config.x1 -= Dx;
    }
    public void xscaleBy(double x) { //scale only represents the lines in the back showing the values
        config.xScale *= x;
    }
    public void yscaleBy(double y) {
        config.yScale *= y;
    }
    public double ppsx() {
        return pxw * config.xScale / (config.x2 - config.x1); //pixels per window * window per x * x per scale = pixels per scale
    }
    public double ppsy() {
        return pxh * config.yScale / (config.y2 - config.y1); //pixels per window * window per y * y per scale = pixels per scale
    }
    public double map(double a1, double a2, double b1, double b2, double n) {
        return (n - a1) / (a2 - a1) * (b2 - b1) + b1; //don't use b1 and b2 swapped, doesn't work correctly, because adds bottom of range (maybe works? idek)
    }
    public int mapx(double n) {
        return (int)map(config.x1, config.x2, 0, pxw, n);
    }
    public int mapy(double n) {
        return (int)map(config.y1, config.y2, pxh, 0, n); //todo fix this it's upside down but if i upside down it fills :((
    }


    public void paintComponent(Graphics g) {
        if (debug) System.out.println("yaa draw");
        super.paintComponent(g);
        drawGraph(g);
    }

    public void drawagain() {
        SwingUtilities.invokeLater(new Runnable() {
            public void run() {
                frame.repaint();
            }
        });
    }

    public void drawGraph(Graphics g) {
        double x1 = config.x1;
        double x2 = config.x2;
        double y1 = config.y1;
        double y2 = config.y2;
        double xratio = x1 / (x1 - x2);
        double yratio = y1 / (y1 - y2);
        var center = new Point((int)(pxw * xratio), pxh - (int)(pxh * yratio));
        
        g.setColor(Color.GRAY.brighter());
        if (x2 > 0) {
            for (int i = 0; i < x2; i += config.xScale) {
                int x = mapx(i);
                g.drawLine(x,0,x,pxh);
            }
        }
        if (x1 < 0) {
            for (int i = 0; i > x1; i -= config.xScale) {
                int x = mapx(i);
                g.drawLine(x,0,x,pxh);
            }
        }
        if (y2 > 0) {
            for (int i = 0; i < y2; i += config.yScale) {
                int y = mapy(i);
                g.drawLine(0,y,pxw,y);
            }
        }
        if (y1 < 0) {
            for (int i = 0; i > y1; i -= config.yScale) {
                int y = mapy(i);
                g.drawLine(0,y,pxw,y);
            }
        }

        g.setColor(Color.BLACK);
        g.drawLine(center.x, 0, center.x, pxh);
        g.drawLine(0, center.y, pxw, center.y);
        for (int j = 0; j < graph.size(); j++) {
            g.setColor(colors.get(j));
            for (int i = 1; i < graph.get(j).size(); i++) {
                double lx1 = graph.get(j).get(i-1).x;
                double lx2 = graph.get(j).get(i).x;
                double ly1 = graph.get(j).get(i-1).y;
                double ly2 = graph.get(j).get(i).y;
                if(debug2) System.out.printf("| %s |", colors.get(j));
                if(debug2) System.out.printf("| about to plot 1 |");
                if(lx1 < config.x1 || lx2 > config.x2 || ly1 < config.y1 || ly2 > config.y2) continue;
                if(debug2) System.out.printf("| about to plot 2 |");
                if(this.jumps.get(j).get(i)) continue;
                if(debug2) System.out.printf("| about to plot 3 |");
                g.drawLine(mapx(lx1), mapy(ly1), mapx(lx2), mapy(ly2));
                if(debug2) System.out.printf("Unmapped #%d : (%f,%f) (%f,%f)\n",i, lx1, ly1, lx2, ly2);
                if(debug2) System.out.printf("Mapped #%d : (%d,%d) (%d,%d)\n",i, mapx(lx1), mapy(ly1), mapx(lx2), mapy(ly2));
            }
        }
    }

    public void init(int pxw, int pxh, String name) {
        this.pxw = pxw;
        this.pxh = pxh;
        this.frame = new JFrame(name);
        this.frame.setSize(pxw, pxh);
        this.frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        this.frame.getContentPane().add(this, BorderLayout.CENTER);
        this.frame.setVisible(true);
        if (debug) System.out.printf("Window output, x1: %f x2: %f y1: %f y2: %f w: %d h: %d plots: %d \n", this.config.x1, this.config.x2, this.config.y1, this.config.y2, this.pxw, this.pxh, graph.size());
    }    

    public static class GraphConfig {
        public double x1; //currently UB if x1 > x2, same with y
        public double x2;
        public double y1;
        public double y2;
        public double xScale;
        public double yScale;
        public double minpps = 5; //minimum pixels per scale
        public int thickness = 3;
        public double scaleby = 2;
        public double extendby = 5;
        public double x1min = Double.MIN_VALUE;
        public double x2max = Double.MAX_VALUE;
        public double y1min = Double.MIN_VALUE;
        public double y2max = Double.MAX_VALUE;

        public GraphConfig(double x1, double x2, double y1, double y2, double xScale, double yScale, double minpps) {
            this.x1 = x1;
            this.x2 = x2;
            this.y1 = y1;
            this.y2 = y2;
            this.xScale = xScale;
            this.yScale = yScale;
        }
        public GraphConfig() {
            this(-10., 10., -10., 10., 1., 1., 5.);
        }
        public static class Builder {
            private double x1; //currently UB if x1 > x2, same with y
            private double x2;
            private double y1;
            private double y2;
            private double xScale;
            private double yScale;
            private double minpps = 5; //minimum pixels per scale
            public Builder() {
                this.x1 = -10.;
                this.x2 = 10.;
                this.y1 = -10.;
                this.y2 = 10.;
                this.xScale = 1.;
                this.yScale = 1.;
            }
            public Builder x1(double x1) {
                this.x1 = x1;
                return this;
            }
            public Builder x2(double x2) {
                this.x2 = x2;
                return this;
            }
            public Builder y1(double y1) {
                this.y1 = y1;
                return this;
            }
            public Builder y2(double y2) {
                this.y2 = y2;
                return this;
            }
            public Builder xScale(double xScale) {
                this.xScale = xScale;
                return this;
            }
            public Builder yScale(double yScale) {
                this.yScale = yScale;
                return this;
            }
            public Builder minpps(double minpps) {
                this.minpps = minpps;
                return this;
            }
            public GraphConfig build() {
                return new GraphConfig(x1, x2, y1, y2, xScale, yScale, minpps);
            }
        }
    }

    public Graph(GraphConfig c) { //scale only represents the lines in the back showing the values
        this.config = c;
        this.graph = new ArrayList<ArrayList<Pair>>();
        this.jumps = new ArrayList<ArrayList<Boolean>>();
        this.colors = new ArrayList<Color>();
        this.frame = null;
        this.pxw = 500;
        this.pxh = 500;
    }
}