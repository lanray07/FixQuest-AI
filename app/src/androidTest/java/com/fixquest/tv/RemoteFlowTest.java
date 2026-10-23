package com.fixquest.tv;

import android.app.*;
import android.os.Bundle;
import android.content.Intent;
import android.view.*;
import android.widget.TextView;
import java.util.*;

/** Runs the shipped UI with real D-pad key events and no test-library dependency. */
public class RemoteFlowTest extends Instrumentation {
    Activity activity;
    int actions;
    StringBuilder report=new StringBuilder();
    boolean demoMode;
    @Override public void onCreate(Bundle arguments){super.onCreate(arguments);demoMode=arguments!=null&&"true".equals(arguments.getString("demo"));start();}
    @Override public void onStart(){Bundle result=new Bundle();try {
        Intent launch=new Intent(Intent.ACTION_MAIN).setClassName(getTargetContext(),"com.fixquest.tv.MainActivity").addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        activity=startActivitySync(launch);waitForIdleSync();capture("01-home");
        if(demoMode){runDemo();result.putString("stream","Tablet rehearsal captured; not Fire TV submission evidence.");finish(Activity.RESULT_OK,result);return;}
        press("How It Works");
        if(has("Narration: ON"))press("Narration: ON");
        press("Speech pace:");press("Speech pace:");press("Back to the house");
        log("Home, help, narration toggle and speech pace reachable by D-pad");
        // Incorrect answer, hint dialog, repeat, and Back cancellation on a real run.
        press("Start Adventure");capture("03-mission-intro");press("Let's investigate");capture("04-tap-question");press("The cupboard handle");capture("05-retry-feedback");check(has("A CHANCE TO LEARN"),"Incorrect feedback missing");press("Try again");press("QuestGuide hint");pressDialog("Back to challenge");press("Repeat");sendKeyDownUpSync(KeyEvent.KEYCODE_BACK);waitForIdleSync();pressDialog("Keep learning");
        solveTap();capture("07-mission-complete");check(has("+150 XP"),"Retry run should award 150 XP");
        log("Tap: wrong-answer retry, hint, repeat, leave cancellation, sequence and 150 XP");
        press("Replay this mission");press("Let's investigate");solveTap();check(has("+180 XP"),"Perfect tap should award 180 XP");
        press("Explore another room");capture("02-missions");press("Enter room",0);press("Let's investigate");
        choose("A slow-draining sink");choose("Hair and soap residue");choose("With gloves, inspect visible stopper debris");
        press("Check that no drain cleaner");press("Remove only accessible");press("Test with a small");press("Next challenge");choose("Use a drain screen");check(has("+180 XP"),"Perfect sink score");
        log("Sink: all five challenges including three-part sequence, 180 XP");
        press("Explore another room");capture("02-missions");press("Enter room",0);press("Let's investigate");
        choose("The gap around");choose("Check whether");choose("A correctly sized");
        capture("06-door-puzzle");press("Check alignment");check(has("A CHANCE TO LEARN"),"Misalignment should fail");press("Try again");
        press("Move marker left");press("Move marker left");press("Move marker left");press("Check alignment");press("Next challenge");choose("Stop and seek");check(has("+150 XP"),"Door retry score");
        press("My Progress");check(has("510 XP"),"Expected aggregate 510 XP, best-score persistence or replay duplication failed");check(has("3 badges collected"),"Three badges not earned");
        log("Door: failed alignment, three adjustments, final question and badge; total 510 XP");
        press("Return home");press("Choose a Mission");press("Replay mission",2);press("Let's investigate");
        choose("The gap around");choose("Check whether");choose("A correctly sized");press("Move marker left");press("Move marker left");press("Move marker left");press("Check alignment");press("Next challenge");choose("Stop and seek");press("My Progress");capture("08-progress");check(has("540 XP"),"Best-score improvement should give 540 total");
        press("Return home");press("Start Adventure");press("Let's investigate");sendKeyDownUpSync(KeyEvent.KEYCODE_BACK);waitForIdleSync();pressDialog("Choose a mission");check(has("Pick a little problem"),"Leave confirmation did not return to missions");sendKeyDownUpSync(KeyEvent.KEYCODE_BACK);waitForIdleSync();check(has("Your home."),"Back from missions failed");
        log("Perfect replay raises total to 540 without farming; Back/leave navigation works");
        check(getTargetContext().getSharedPreferences("quest_progress",0).getInt("xp_tap",0)==180,"Tap record missing");
        log("PASS: "+actions+" Select actions; every target reached by directional focus search and real D-pad events");
        result.putString("stream",report.toString());finish(Activity.RESULT_OK,result);
    } catch(Throwable t){result.putString("stream",report+"\nFAIL: "+t.toString());finish(Activity.RESULT_CANCELED,result);}}
    void solveTap(){choose("The kitchen tap");choose("A worn internal");capture("09-toolkit");choose("An adjustable spanner");press("Isolate the water");press("Open the tap to verify");press("In the model, replace");press("Next challenge");choose("Stop and ask");}
    long demoStart;
    org.json.JSONArray chapters=new org.json.JSONArray();
    void shot(String caption,int seconds) throws Exception {
        double start=(android.os.SystemClock.elapsedRealtime()-demoStart)/1000.0;
        org.json.JSONObject chapter=new org.json.JSONObject();chapter.put("start",start);chapter.put("text",caption);chapter.put("duration",seconds);chapters.put(chapter);
        log("Rehearsal: "+caption);Thread.sleep(seconds*1000L);
    }
    void runDemo() throws Exception {
        press("How It Works");if(has("Narration: ON"))press("Narration: ON");press("Back to the house");
        android.os.ParcelFileDescriptor recording=getUiAutomation().executeShellCommand("screenrecord --time-limit 175 --bit-rate 6000000 /sdcard/fixquest-rehearsal.mp4");
        demoStart=android.os.SystemClock.elapsedRealtime();
        shot("Meet FixQuest AI. Turn everyday home problems into interactive repair adventures. This recording uses an Amazon Fire tablet.",10);
        press("Choose a Mission");shot("Three rooms, three quests: a dripping tap, a slow sink, and a sticking door. Every action works with directional controls.",8);
        press("Enter room",0);shot("Start with a model tap. Observe the scenario, consider the clues, and learn when to stop for qualified help.",8);
        press("Let's investigate");shot("The focus ring shows the selected answer. Use the D-pad to move and Select to make a choice.",7);
        press("The cupboard handle");shot("A wrong answer is a chance to learn. The guide gives a contextual nudge, and you can try again.",8);
        press("Try again");press("QuestGuide hint");shot("QuestGuide offers curated guidance offline. It is deterministic, not live generative AI. Prompts can use an installed offline system voice.",8);pressDialog("Back to challenge");
        press("The kitchen tap");shot("First, identify where the drip appears. Observation comes before guessing at a hidden problem.",6);press("Next challenge");
        press("A worn internal");shot("The model explains a likely cause, while recognising that different tap designs use different parts.",6);press("Next challenge");
        shot("Original illustrations and a simple toolkit make each decision visual. Here, choose a fitted spanner with a protective cloth.",8);choose("An adjustable spanner");
        shot("Now build a safe sequence in the model: isolate the water, verify flow has stopped, then replace the matching washer.",8);
        press("Isolate the water");press("Open the tap to verify");press("In the model, replace");shot("The puzzle rewards the order of the checks. If isolation is uncertain, stop and get help.",6);press("Next challenge");
        choose("Stop and ask");shot("Mission complete. Earn a badge and experience points. The best completed result is saved locally, and retries remain part of learning.",10);
        press("Explore another room");press("Enter room",1);press("Let's investigate");choose("The gap around");choose("Check whether");choose("A correctly sized");
        shot("Another room brings another interaction. Align the virtual door marker with its target. This is a simulation, not a diagnosis.",8);
        press("Move marker left");Thread.sleep(800);press("Move marker left");Thread.sleep(800);press("Move marker left");press("Check alignment");shot("Small remote movements bring the model into alignment. The explanation reminds us that real doors can stick for several reasons.",8);press("Next challenge");choose("Stop and seek");
        shot("Knowing when a problem needs professional assessment is part of the game. Complete a quest to unlock its badge.",8);
        press("My Progress");shot("XP and badges stay on this device. Replaying can improve a best score, without counting the same mission again and again.",10);
        press("Return home");shot("No login, backend, or API key. FixQuest AI makes home knowledge a shared adventure. Fire TV demo footage is still required.",8);
        java.io.File folder=new java.io.File(getTargetContext().getExternalFilesDir(null),"demo");folder.mkdirs();
        org.json.JSONObject output=new org.json.JSONObject();output.put("chapters",chapters);output.put("end",(android.os.SystemClock.elapsedRealtime()-demoStart)/1000.0);
        try(java.io.FileOutputStream out=new java.io.FileOutputStream(new java.io.File(folder,"chapters.json"))){out.write(output.toString(2).getBytes(java.nio.charset.StandardCharsets.UTF_8));}
        long remaining=175000-(android.os.SystemClock.elapsedRealtime()-demoStart);while(remaining>0){Thread.sleep(Math.min(remaining,5000));remaining=175000-(android.os.SystemClock.elapsedRealtime()-demoStart);}recording.close();
    }
    void capture(String name){try {Thread.sleep(400);waitForIdleSync();java.io.File dir=new java.io.File(getTargetContext().getExternalFilesDir(null),"screenshots");dir.mkdirs();android.graphics.Bitmap bitmap=getUiAutomation().takeScreenshot();try(java.io.FileOutputStream out=new java.io.FileOutputStream(new java.io.File(dir,name+".png"))){bitmap.compress(android.graphics.Bitmap.CompressFormat.PNG,100,out);}bitmap.recycle();}catch(Exception e){throw new AssertionError("Screenshot failed",e);}}
    void choose(String label){press(label);check(has("WELL REASONED"),"Expected correct feedback for "+label);press(has("Complete mission")?"Complete mission":"Next challenge");}
    void log(String s){report.append(s).append('\n');Bundle progress=new Bundle();progress.putString("stream",s+"\n");sendStatus(0,progress);}
    void check(boolean condition,String message){if(!condition)throw new AssertionError(message);}
    void walk(View v,List<View> out){out.add(v);if(v instanceof ViewGroup){ViewGroup g=(ViewGroup)v;for(int i=0;i<g.getChildCount();i++)walk(g.getChildAt(i),out);}}
    View decor(){final View[] out={null};runOnMainSync(()->out[0]=activity.getWindow().getDecorView());return out[0];}
    boolean has(String prefix){final boolean[] found={false};runOnMainSync(()->{List<View> all=new ArrayList<>();walk(activity.getWindow().getDecorView(),all);for(View v:all)if(v instanceof TextView&&((TextView)v).getText().toString().contains(prefix))found[0]=true;});return found[0];}
    void press(String label){press(label,0);}
    void pressDialog(String label) throws Exception {
        // Dialog window attachment is asynchronous; poll its public accessibility tree.
        for(int attempt=0;attempt<20;attempt++){
            android.view.accessibility.AccessibilityNodeInfo root=getUiAutomation().getRootInActiveWindow();
            if(root!=null){
                for(android.view.accessibility.AccessibilityNodeInfo node:root.findAccessibilityNodeInfosByText(label)){
                    if(!node.isFocusable())continue;
                    android.view.accessibility.AccessibilityNodeInfo focused=root.findFocus(android.view.accessibility.AccessibilityNodeInfo.FOCUS_INPUT);
                    if(focused!=null&&focused.getText()!=null&&focused.getText().toString().equalsIgnoreCase(label)){sendKeyDownUpSync(KeyEvent.KEYCODE_DPAD_CENTER);waitForIdleSync();actions++;return;}
                    android.graphics.Rect targetBounds=new android.graphics.Rect(),focusBounds=new android.graphics.Rect();node.getBoundsInScreen(targetBounds);if(focused!=null)focused.getBoundsInScreen(focusBounds);
                    sendKeyDownUpSync(targetBounds.centerX()<focusBounds.centerX()?KeyEvent.KEYCODE_DPAD_LEFT:KeyEvent.KEYCODE_DPAD_RIGHT);waitForIdleSync();
                }
            }
            Thread.sleep(100);
        }
        throw new AssertionError("Dialog action unreachable: "+label);
    }
    void press(String label,int occurrence){
        waitForIdleSync();final List<Integer> path=new ArrayList<>();final String[] problem={null};
        runOnMainSync(()->{
            // Dialog windows own the focused decor when visible.
            View root=activity.getWindow().getDecorView();
            List<View> all=new ArrayList<>();walk(root,all);View target=null;int matched=0;
            for(View v:all)if(v.isFocusable()&&v instanceof TextView&&((TextView)v).getText().toString().contains(label)){if(matched++==occurrence){target=v;break;}}
            if(target==null){problem[0]="Missing focusable action: "+label+" #"+occurrence;return;}
            View focused=root.findFocus();if(focused==null){problem[0]="No default focus for "+label;return;}
            Map<View,List<Integer>> paths=new IdentityHashMap<>();ArrayDeque<View> queue=new ArrayDeque<>();paths.put(focused,new ArrayList<>());queue.add(focused);
            int[] directions={View.FOCUS_DOWN,View.FOCUS_UP,View.FOCUS_RIGHT,View.FOCUS_LEFT};int[] keys={KeyEvent.KEYCODE_DPAD_DOWN,KeyEvent.KEYCODE_DPAD_UP,KeyEvent.KEYCODE_DPAD_RIGHT,KeyEvent.KEYCODE_DPAD_LEFT};
            while(!queue.isEmpty()){View v=queue.remove();if(v==target){path.addAll(paths.get(v));return;}for(int i=0;i<4;i++){View next=v.focusSearch(directions[i]);if(next!=null&&!paths.containsKey(next)){List<Integer> p=new ArrayList<>(paths.get(v));p.add(keys[i]);paths.put(next,p);queue.add(next);}}}problem[0]="D-pad cannot reach "+label;
        });
        check(problem[0]==null,problem[0]);for(int key:path){sendKeyDownUpSync(key);waitForIdleSync();}sendKeyDownUpSync(KeyEvent.KEYCODE_DPAD_CENTER);waitForIdleSync();actions++;
    }
}





