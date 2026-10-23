package com.fixquest.tv;

import android.content.Context;
import android.graphics.*;
import android.view.View;

/** Original vector-like illustrations, drawn locally at any TV resolution. */
public class SceneView extends View {
    final Paint p=new Paint(3); final String type; Canvas c; public int alignment=3; public boolean toolkit;
    final int navy=Color.rgb(9,21,34), teal=Color.rgb(66,232,196), amber=Color.rgb(255,204,115), wall=Color.rgb(39,69,85), pale=Color.rgb(191,217,222);
    public SceneView(Context context,String type){super(context);this.type=type;setImportantForAccessibility(IMPORTANT_FOR_ACCESSIBILITY_YES);setContentDescription(type.equals("house")?"Illustrated home with kitchen, bathroom and hallway learning quests":type.equals("tap")?"Kitchen tap dripping blue water into a basin":type.equals("sink")?"Bathroom basin with standing water near its drain":type.equals("door")?"Interior door with uneven gaps and an alignment marker":"A golden achievement badge");}
    int col(String v){return Color.parseColor(v);}
    void box(float x,float y,float w,float h,float r,int color){p.setColor(color);p.setStyle(Paint.Style.FILL);c.drawRoundRect(x,y,x+w,y+h,r,r,p);}
    void line(float x,float y,float x2,float y2,float width,int color){p.setColor(color);p.setStrokeWidth(width);p.setStrokeCap(Paint.Cap.ROUND);c.drawLine(x,y,x2,y2,p);}
    void circle(float x,float y,float r,int color){p.setColor(color);p.setStyle(Paint.Style.FILL);c.drawCircle(x,y,r,p);}
    void label(String value,float x,float y,float size,int color){p.setTypeface(Typeface.create("sans-serif-medium",Typeface.NORMAL));p.setTextSize(size);p.setColor(color);c.drawText(value,x,y,p);}
    void poly(int color,float... xy){Path path=new Path();path.moveTo(xy[0],xy[1]);for(int i=2;i<xy.length;i+=2)path.lineTo(xy[i],xy[i+1]);path.close();p.setColor(color);p.setStyle(Paint.Style.FILL);c.drawPath(path,p);}
    void spark(float x,float y,float s){poly(amber,x,y-s,x+s*.3f,y-s*.3f,x+s,y,x+s*.3f,y+s*.3f,x,y+s,x-s*.3f,y+s*.3f,x-s,y,x-s*.3f,y-s*.3f);}
    @Override protected void onDraw(Canvas canvas){super.onDraw(canvas);c=canvas;float scale=Math.min(getWidth()/500f,getHeight()/450f);c.save();c.translate((getWidth()-500*scale)/2,(getHeight()-450*scale)/2);c.scale(scale,scale);
        p.setShader(new RadialGradient(260,210,230,new int[]{col("#194653"),navy},null,Shader.TileMode.CLAMP));c.drawRect(0,0,500,450,p);p.setShader(null);
        if(type.equals("house"))house();else if(type.equals("celebrate"))celebrate();else room();if(toolkit)tools();c.restore();}
    void tools(){
        box(45,333,410,100,12,col("#102633"));label("THE MODEL TOOLKIT",62,353,11,teal);
        for(int i=0;i<3;i++){float x=83+i*143;box(x-22,364,98,58,7,col("#254451"));label(""+(char)('A'+i),x-14,414,10,amber);}
        if(type.equals("tap")){
            line(113,377,113,405,6,col("#B58B62"));box(97,371,32,11,3,pale);
            knife(254);spanner(397);
        }else {knife(111);spanner(254);line(397,372,397,393,4,pale);box(390,390,14,22,5,amber);}
    }
    void knife(float x){poly(pale,x-5,371,x+5,371,x+5,395,x-5,390);box(x-5,395,10,17,3,col("#B58B62"));}
    void spanner(float x){line(x,383,x,409,7,pale);circle(x,376,12,pale);poly(col("#254451"),x-8,365,x+8,365,x+5,379,x-5,379);}
    void house(){
        circle(404,72,28,col("#203E4D"));circle(397,65,25,col("#FFCC73"));spark(88,100,9);spark(435,174,7);
        box(48,370,410,10,5,col("#203C47"));
        // Warm cutaway, framed by a teal roof and visible floor lines.
        box(112,170,286,200,6,col("#173243"));poly(col("#26505D"),88,177,255,57,422,177);poly(teal,85,171,255,45,425,171,415,185,255,69,95,185);
        box(135,180,121,86,3,col("#315766"));box(270,180,106,86,3,col("#354D59"));box(135,282,121,76,3,col("#283E50"));box(270,282,106,76,3,col("#43606A"));
        box(185,109,40,44,4,amber);line(205,111,205,152,4,wall);line(187,130,223,130,4,wall);
        box(143,231,103,23,3,col("#67898F"));box(140,226,109,8,3,pale);box(166,222,45,5,2,teal);line(188,226,188,207,4,pale);line(188,207,200,207,4,pale);
        box(295,224,63,15,5,pale);line(324,224,324,205,4,pale);line(324,205,337,205,4,pale);box(301,191,50,7,3,col("#7EBDFF"));
        box(176,297,42,61,3,amber);box(180,301,34,57,2,col("#936C4A"));circle(207,333,3,amber);
        box(281,318,82,7,2,pale);box(293,325,59,29,3,col("#30625D"));circle(310,337,3,teal);circle(335,337,3,teal);
        box(253,174,8,186,2,col("#142C3B"));box(129,267,252,10,2,col("#142C3B"));
        box(70,321,30,50,7,col("#B2845B"));line(85,325,85,281,4,teal);circle(73,292,13,col("#238E80"));circle(95,281,15,col("#42BBA4"));circle(76,269,12,teal);
        box(127,394,264,33,16,col("#143440"));circle(146,410,4,teal);label("3 ROOMS. A WORLD OF KNOW-HOW.",160,415,11,pale);
        label("01",145,199,10,teal);label("02",280,199,10,amber);label("03",145,300,10,col("#7EBDFF"));
    }
    void room(){
        box(30,42,440,337,20,col("#132D3B"));box(46,58,408,267,12,wall);box(46,325,408,38,5,col("#1C3545"));
        if(type.equals("door")){door();return;}
        // Subtle tile grid and a sunlit window establish room depth.
        for(int i=0;i<6;i++)line(48,90+i*43,452,90+i*43,1,col("#335563"));for(int i=0;i<7;i++)line(65+i*63,60,65+i*63,320,1,col("#335563"));
        box(77,82,105,100,6,col("#1A3847"));box(84,89,91,86,4,col("#77B5C1"));line(129,90,129,173,5,pale);line(85,132,174,132,5,pale);circle(158,105,11,amber);
        box(80,286,334,47,5,col("#35616C"));box(84,333,326,23,2,col("#284A59"));line(246,290,246,351,3,col("#193847"));line(155,303,181,303,4,pale);line(304,303,330,303,4,pale);
        box(69,269,359,16,5,col("#97B6B8"));
        box(192,253,169,28,9,pale);box(202,258,149,14,7,col("#37677A"));
        // Tap with an open arched spout.
        line(296,254,296,211,10,pale);p.setColor(pale);p.setStyle(Paint.Style.STROKE);p.setStrokeWidth(10);c.drawArc(257,185,296,231,180,180,false,p);p.setStyle(Paint.Style.FILL);line(257,207,257,220,10,pale);line(296,231,313,228,6,teal);
        if(type.equals("tap")){drop(257,234,6);drop(257,250,4);label("OBSERVE THE DRIP",167,410,14,teal);circle(372,214,22,col("#173D48"));label("?",366,222,24,amber);}
        else {box(207,257,137,12,6,col("#69B4D0"));circle(270,263,7,col("#1B3B4C"));line(264,261,278,266,2,col("#C8996F"));line(267,267,275,257,2,col("#C8996F"));label("FOLLOW THE FLOW",169,410,14,col("#7EBDFF"));}
        box(369,234,24,35,4,col("#AB8260"));line(381,234,381,208,3,teal);circle(373,212,8,col("#31AD95"));circle(387,205,9,teal);
        label(type.equals("tap")?"KITCHEN / 01":"BATHROOM / 02",65,35,12,pale);
    }
    void drop(float x,float y,float s){poly(col("#7EBDFF"),x,y-s,x+s*.7f,y+s*.3f,x+s*.5f,y+s,x-s*.5f,y+s,x-s*.7f,y+s*.3f);}
    void door(){
        box(155,78,194,247,4,col("#142C3A"));box(166,88,171,237,2,col("#D8AD79"));
        c.save();c.rotate(alignment*0.5f,177,100);box(175,95,149,231,3,col("#9B7755"));box(189,111,121,88,4,col("#AD8861"));box(189,214,121,89,4,col("#AD8861"));line(300,205,311,205,5,amber);circle(301,205,5,amber);box(171,122,8,22,2,pale);box(171,268,8,22,2,pale);c.restore();
        line(80,218,125,218,2,teal);circle(78,218,7,teal);label("CHECK",62,241,10,pale);label("THE GAPS",56,256,10,pale);
        label("HALLWAY / 03",65,35,12,pale);
        box(120,384,262,44,14,col("#173441"));line(144,405,356,405,4,col("#375565"));box(242,393,16,24,4,amber);circle(250+alignment*29,405,8,teal);label("−",130,408,12,pale);label("+",365,409,12,pale);
    }
    void celebrate(){circle(250,206,142,col("#123440"));circle(250,206,111,col("#1D4A50"));poly(col("#BB8060"),193,255,177,365,223,343,251,370,266,275);poly(col("#E4AD62"),246,274,264,371,289,342,330,358,306,254);circle(250,204,83,amber);circle(250,204,69,col("#DFAC61"));circle(250,204,59,amber);line(216,206,239,228,12,navy);line(239,228,281,182,12,navy);spark(95,159,15);spark(398,244,17);spark(356,93,12);circle(145,306,5,teal);circle(388,130,4,teal);label("A LITTLE WISER. A LEVEL HIGHER.",129,417,12,pale);}
}

