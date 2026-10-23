package com.fixquest.tv;

import android.app.Activity;
import android.app.AlertDialog;
import android.os.Bundle;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.graphics.Typeface;
import android.graphics.drawable.GradientDrawable;
import android.speech.tts.TextToSpeech;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.*;
import org.json.*;
import java.nio.charset.StandardCharsets;
import java.util.*;

/** Dependency-free TV interface. Every action is a native focusable control. */
public class MainActivity extends Activity {
    static final int BG=Color.rgb(9,21,34), PANEL=Color.rgb(17,38,53), TEAL=Color.rgb(66,232,196), WHITE=Color.rgb(241,247,247), MUTED=Color.rgb(165,188,201), AMBER=Color.rgb(255,204,115);
    JSONArray missions;
    SharedPreferences prefs;
    TextToSpeech speech;
    boolean speechReady, narration;
    float u;
    LinearLayout root, body;
    LinearLayout questionPane;
    SceneView scene;
    String screen="home", spoken="", feedback="";
    int missionIndex, stepIndex, mistakes, sequence, alignment=3;
    boolean answered, feedbackCorrect;
    final Set<Integer> solved = new HashSet<>();
    View firstFocus;

    @Override public void onCreate(Bundle state) {
        super.onCreate(state);
        getWindow().getDecorView().setSystemUiVisibility(5894);
        prefs=getSharedPreferences("quest_progress",MODE_PRIVATE);
        narration=prefs.getBoolean("narration",true);
        try { missions=new JSONArray(new String(readMissionBytes(),StandardCharsets.UTF_8)); }
        catch(Exception e) { throw new IllegalStateException("Mission data could not load",e); }
        speech=new TextToSpeech(this,status->{
            speechReady=status==TextToSpeech.SUCCESS;
            if(speechReady){
                int language=speech.setLanguage(Locale.UK);
                if(language<TextToSpeech.LANG_AVAILABLE)language=speech.setLanguage(Locale.US);
                speechReady=language>=TextToSpeech.LANG_AVAILABLE;
                if(speechReady){
                    android.speech.tts.Voice localVoice=null;
                    Set<android.speech.tts.Voice> voices=speech.getVoices();
                    if(voices!=null)for(android.speech.tts.Voice voice:voices)if(!voice.isNetworkConnectionRequired()&&voice.getLocale().getLanguage().equals("en")){localVoice=voice;if(voice.getLocale().equals(Locale.UK))break;}
                    if(localVoice!=null)speechReady=speech.setVoice(localVoice)==TextToSpeech.SUCCESS;
                    else if(speech.getVoice()==null||speech.getVoice().isNetworkConnectionRequired())speechReady=false;
                }
                speech.setSpeechRate(prefs.getFloat("rate",0.9f));say(spoken);
            }
        });
        if(state!=null){ screen=state.getString("screen","home"); missionIndex=state.getInt("mission"); stepIndex=state.getInt("step"); mistakes=state.getInt("mistakes"); sequence=state.getInt("sequence"); alignment=state.getInt("alignment",3); answered=state.getBoolean("answered"); feedback=state.getString("feedback",""); feedbackCorrect=state.getBoolean("feedbackCorrect"); int[] s=state.getIntArray("solved"); if(s!=null)for(int n:s)solved.add(n); }
        render();
    }
    @Override public void onSaveInstanceState(Bundle out){super.onSaveInstanceState(out);out.putString("screen",screen);out.putInt("mission",missionIndex);out.putInt("step",stepIndex);out.putInt("mistakes",mistakes);out.putInt("sequence",sequence);out.putInt("alignment",alignment);out.putBoolean("answered",answered);out.putString("feedback",feedback);out.putBoolean("feedbackCorrect",feedbackCorrect);out.putIntArray("solved",solvedArray());}
    JSONObject mission(){return missions.optJSONObject(missionIndex);}
    byte[] readMissionBytes() throws java.io.IOException {try(java.io.InputStream in=getAssets().open("missions.json");java.io.ByteArrayOutputStream out=new java.io.ByteArrayOutputStream()){byte[] b=new byte[4096];int n;while((n=in.read(b))!=-1)out.write(b,0,n);return out.toByteArray();}}
    int[] solvedArray(){int[] result=new int[solved.size()];int i=0;for(int n:solved)result[i++]=n;return result;}
    String dots(int step){StringBuilder b=new StringBuilder();for(int i=0;i<5;i++)b.append(i<=step?'●':'○');return b.toString();}
    JSONObject step(){return mission().optJSONArray("steps").optJSONObject(stepIndex);}
    int px(float v){return Math.round(v*u);}
    TextView text(String value,float size,int color){TextView t=new TextView(this);t.setText(value);t.setTextColor(color);t.setTextSize(android.util.TypedValue.COMPLEX_UNIT_PX,px(size)*getResources().getConfiguration().fontScale);t.setIncludeFontPadding(false);t.setLineSpacing(px(3),1);return t;}
    TextView heading(String value,float size){TextView t=text(value,size,WHITE);t.setTypeface(Typeface.create("sans-serif",Typeface.BOLD));return t;}
    LinearLayout column(){LinearLayout l=new LinearLayout(this);l.setOrientation(LinearLayout.VERTICAL);return l;}
    LinearLayout row(){LinearLayout l=new LinearLayout(this);l.setOrientation(LinearLayout.HORIZONTAL);l.setGravity(Gravity.CENTER_VERTICAL);return l;}
    void gap(LinearLayout l,int h){View v=new View(this);l.addView(v,new LinearLayout.LayoutParams(1,px(h)));}
    GradientDrawable shape(int fill,int stroke){GradientDrawable d=new GradientDrawable();d.setColor(fill);d.setCornerRadius(px(13));if(stroke!=0)d.setStroke(px(2),stroke);return d;}
    Button button(String label,Runnable action){Button b=new Button(this);b.setFocusableInTouchMode(true);b.setText(label);b.setAllCaps(false);b.setTextSize(android.util.TypedValue.COMPLEX_UNIT_PX,px(18));b.setTypeface(Typeface.create("sans-serif-medium",Typeface.NORMAL));b.setTextColor(WHITE);b.setGravity(Gravity.CENTER_VERTICAL|Gravity.START);b.setPadding(px(18),px(9),px(18),px(9));b.setMinHeight(px(43));b.setMinimumHeight(px(43));b.setStateListAnimator(null);b.setBackground(shape(PANEL,Color.rgb(45,72,89)));b.setOnFocusChangeListener((v,has)->{b.setBackground(shape(has?TEAL:PANEL,has?WHITE:Color.rgb(45,72,89)));b.setTextColor(has?BG:WHITE);b.setElevation(has?px(6):0);});b.setOnClickListener(v->action.run());LinearLayout.LayoutParams lp=new LinearLayout.LayoutParams(-1,-2);lp.bottomMargin=px(8);b.setLayoutParams(lp);if(firstFocus==null)firstFocus=b;return b;}
    void addButton(LinearLayout l,String label,Runnable action){l.addView(button(label,action));}
    void smallButtons(LinearLayout parent){LinearLayout r=row();Button hint=button("QuestGuide hint",()->hint());r.addView(hint,new LinearLayout.LayoutParams(0,-2,1));View spacer=new View(this);r.addView(spacer,new LinearLayout.LayoutParams(px(8),1));Button repeat=button("Repeat",()->say(spoken));r.addView(repeat,new LinearLayout.LayoutParams(0,-2,0.65f));parent.addView(r);}
    void say(String value){spoken=value;if(narration&&speechReady&&value!=null&&!value.isEmpty())speech.speak(value,TextToSpeech.QUEUE_FLUSH,null,"quest");}
    void go(String destination){screen=destination;feedback="";render();}
    void render(){
        if(speechReady)speech.stop();
        u=getResources().getDisplayMetrics().widthPixels/1100f;
        firstFocus=null;
        root=column();root.setPadding(px(42),px(24),px(42),px(18));root.setBackgroundColor(BG);setContentView(root);
        LinearLayout header=row();TextView brand=heading("⌂  FIXQUEST",23);brand.setTextColor(TEAL);header.addView(brand);TextView ai=text("  AI  /  HOME SKILLS, LEVELLED UP",11,MUTED);header.addView(ai,new LinearLayout.LayoutParams(0,-2,1));TextView total=text(totalXp()+" XP   •   "+completed()+" / 3 badges",15,AMBER);header.addView(total);root.addView(header);gap(root,18);
        body=column();root.addView(body,new LinearLayout.LayoutParams(-1,0,1));
        switch(screen){case "missions":showMissions();break;case "intro":showIntro();break;case "play":showPlay();break;case "complete":showComplete();break;case "progress":showProgress();break;case "help":showHelp();break;default:showHome();}
        gap(root,10);LinearLayout footer=row();TextView keys=text("D-PAD  Move     SELECT  Choose     BACK  Return",12,MUTED);footer.addView(keys,new LinearLayout.LayoutParams(0,-2,1));TextView offline=text("OFFLINE READY   •   NO ACCOUNT NEEDED",11,TEAL);footer.addView(offline);root.addView(footer);
        if(firstFocus!=null)firstFocus.requestFocus();
    }
    int totalXp(){int n=0;for(int i=0;i<missions.length();i++)n+=prefs.getInt("xp_"+missions.optJSONObject(i).optString("id"),0);return n;}
    int completed(){int n=0;for(int i=0;i<missions.length();i++)if(prefs.getBoolean("done_"+missions.optJSONObject(i).optString("id"),false))n++;return n;}
    LinearLayout split(){LinearLayout r=row();r.setGravity(Gravity.TOP);body.addView(r,new LinearLayout.LayoutParams(-1,-1));return r;}
    void addScene(LinearLayout r,String type,float weight){scene=new SceneView(this,type);r.addView(scene,new LinearLayout.LayoutParams(0,-1,weight));}
    void showHome(){
        LinearLayout r=split();LinearLayout left=column();left.setGravity(Gravity.CENTER_VERTICAL);r.addView(left,new LinearLayout.LayoutParams(0,-1,0.46f));
        left.addView(text("SMALL FIXES. BIG CONFIDENCE.",13,AMBER));gap(left,13);left.addView(heading("Your home.\nYour next quest.",43));gap(left,12);left.addView(text("Turn everyday home problems into\ninteractive repair adventures.",19,MUTED));gap(left,23);
        addButton(left,"Start Adventure   →",()->start(nextMission()));addButton(left,"Choose a Mission",()->go("missions"));
        LinearLayout actions=row();Button progress=button("My Progress",()->go("progress"));actions.addView(progress,new LinearLayout.LayoutParams(0,-2,1));View spacer=new View(this);actions.addView(spacer,new LinearLayout.LayoutParams(px(8),1));actions.addView(button("How It Works",()->go("help")),new LinearLayout.LayoutParams(0,-2,1));left.addView(actions);
        addScene(r,"house",0.54f);say("Welcome to FixQuest. Turn everyday home problems into interactive repair adventures. Choose Start Adventure, or explore three missions.");
    }
    int nextMission(){for(int i=0;i<3;i++)if(!prefs.getBoolean("done_"+missions.optJSONObject(i).optString("id"),false))return i;return 0;}
    void showMissions(){
        body.addView(text("THE HOUSE IS YOUR CLASSROOM",13,TEAL));gap(body,8);body.addView(heading("Pick a little problem. Learn a lasting skill.",31));gap(body,17);
        LinearLayout cards=row();body.addView(cards,new LinearLayout.LayoutParams(-1,0,1));
        for(int i=0;i<missions.length();i++){final int index=i;JSONObject m=missions.optJSONObject(i);LinearLayout card=column();card.setPadding(px(12),px(12),px(12),px(6));card.setBackground(shape(PANEL,0));LinearLayout.LayoutParams lp=new LinearLayout.LayoutParams(0,-1,1);if(i<2)lp.rightMargin=px(16);cards.addView(card,lp);SceneView art=new SceneView(this,m.optString("id"));card.addView(art,new LinearLayout.LayoutParams(-1,0,1));card.addView(text("0"+(i+1)+"  /  "+m.optString("room"),12,TEAL));gap(card,7);card.addView(heading(m.optString("title"),23));gap(card,8);card.addView(text(m.optString("minutes")+"   •   5 challenges   •   180 XP max",13,MUTED));gap(card,12);addButton(card,prefs.getBoolean("done_"+m.optString("id"),false)?"Replay mission   ↻":"Enter room   →",()->start(index));}
        say("Choose a mission. The Dripping Tap, The Slow Sink, or The Sticking Door.");
    }
    void start(int index){missionIndex=index;stepIndex=0;mistakes=0;sequence=0;alignment=3;solved.clear();answered=false;feedback="";go("intro");}
    LinearLayout contentSide(LinearLayout r){LinearLayout outer=column();outer.setPadding(px(24),px(4),0,0);r.addView(outer,new LinearLayout.LayoutParams(0,-1,0.53f));ScrollView scroll=new ScrollView(this);scroll.setFillViewport(true);outer.addView(scroll,new LinearLayout.LayoutParams(-1,-1));LinearLayout inner=column();scroll.addView(inner);return inner;}
    void showIntro(){LinearLayout r=split();addScene(r,mission().optString("id"),0.47f);LinearLayout right=contentSide(r);right.addView(text(mission().optString("room")+"   /   MISSION 0"+(missionIndex+1),13,TEAL));gap(right,13);right.addView(heading(mission().optString("title"),36));gap(right,15);right.addView(text(mission().optString("intro"),21,WHITE));gap(right,18);right.addView(text("LEARN BY DOING",12,AMBER));gap(right,8);right.addView(text("Observe → decide → practise → remember\n5 challenges  •  up to 180 XP  •  "+mission().optString("minutes"),17,MUTED));gap(right,18);right.addView(text("Educational simulation. Real repairs depend on the fixture and your skills. Stop when unsure and get qualified help.",15,MUTED));gap(right,20);addButton(right,"Let's investigate   →",()->go("play"));addButton(right,"Choose another mission",()->go("missions"));say(mission().optString("title")+". "+mission().optString("intro"));}
    void showPlay(){
        LinearLayout r=split();LinearLayout left=column();r.addView(left,new LinearLayout.LayoutParams(0,-1,0.47f));scene=new SceneView(this,mission().optString("id"));scene.alignment=alignment;scene.toolkit=stepIndex==2&&!mission().optString("id").equals("sink");left.addView(scene,new LinearLayout.LayoutParams(-1,0,1));
        LinearLayout guide=column();guide.setPadding(px(17),px(14),px(17),px(14));guide.setBackground(shape(PANEL,0));guide.addView(text("✦  QUESTGUIDE",13,TEAL));gap(guide,6);guide.addView(text("Curious is a great place to start.\nObserve first. Choose carefully. Learn safely.",17,WHITE));gap(guide,6);guide.addView(text("Smart Learning Guide  •  curated, offline",11,MUTED));left.addView(guide);
        questionPane=contentSide(r);renderQuestion();
    }
    void renderQuestion(){
        LinearLayout q=questionPane;q.removeAllViews();firstFocus=null;scene.toolkit=stepIndex==2&&!mission().optString("id").equals("sink");scene.invalidate();
        q.addView(text("CHALLENGE "+(stepIndex+1)+" OF 5     "+dots(stepIndex),13,TEAL));gap(q,10);q.addView(heading(step().optString("title"),30));gap(q,12);
        if(!feedback.isEmpty()){
            q.addView(text(feedbackCorrect?"✓  WELL REASONED   +20 XP":"↻  A CHANCE TO LEARN",15,feedbackCorrect?TEAL:AMBER));gap(q,13);q.addView(text(feedback,21,WHITE));gap(q,20);
            addButton(q,feedbackCorrect?(stepIndex==4?"Complete mission   →":"Next challenge   →"):"Try again   ↻",()->{if(feedbackCorrect){if(stepIndex==4){complete();return;}stepIndex++;sequence=0;alignment=3;answered=false;scene.alignment=alignment;scene.invalidate();}feedback="";renderQuestion();});
            smallButtons(q);say((feedbackCorrect?"Well reasoned. ":"Let's try again. ")+feedback);
        }else{
            String prompt=step().optString("prompt");q.addView(text(prompt,21,WHITE));gap(q,16);
            if(step().optBoolean("alignment")){
                q.addView(text("ALIGNMENT   "+(alignment>0?"+":"")+alignment+"     •     TARGET  0",16,AMBER));gap(q,9);
                addButton(q,"←  Move marker left",()->adjust(-1));addButton(q,"Move marker right  →",()->adjust(1));addButton(q,"Check alignment   ✓",()->answer(alignment==0));
            }else if(step().has("puzzle")){
                JSONArray tiles=step().optJSONArray("puzzle");q.addView(text("SEQUENCE   "+sequence+" / 3 placed",13,AMBER));gap(q,9);
                // Fixed shuffled order; removed tiles make the sequence visibly build.
                int[] order={2,0,1};for(int j:order){if(j<sequence)continue;final int choice=j;addButton(q,tiles.optString(j),()->{if(choice==sequence){sequence++;if(sequence==3)answer(true);else {renderQuestion();say("Step placed. Choose the next action.");}}else answer(false);});}
            }else{
                JSONArray choices=step().optJSONArray("choices");for(int i=0;i<choices.length();i++){final int answer=i;addButton(q,choices.optString(i),()->answer(answer==step().optInt("answer")));}
            }
            gap(q,7);smallButtons(q);say(prompt);
        }
        if(firstFocus!=null)firstFocus.requestFocus();
    }
    void adjust(int delta){alignment=Math.max(-3,Math.min(3,alignment+delta));scene.alignment=alignment;scene.invalidate();View focused=getCurrentFocus();int index=focused==null?-1:questionPane.indexOfChild(focused);renderQuestion();if(index>=0&&index<questionPane.getChildCount())questionPane.getChildAt(index).requestFocus();}
    void answer(boolean correct){feedbackCorrect=correct;if(correct){solved.add(stepIndex);answered=true;feedback=step().optString("explanation");}else{mistakes++;feedback="That choice does not fit this model. "+step().optString("hint")+" You can try again; learning continues.";}renderQuestion();}
    void hint(){String value=step().optString("hint");say(value);new AlertDialog.Builder(this).setTitle("QuestGuide • a little nudge").setMessage(value+"\n\nCurated guidance • available offline").setPositiveButton("Back to challenge",(d,w)->say(step().optString("prompt"))).show();}
    void complete(){String id=mission().optString("id");int xp=solved.size()*20+50+(mistakes==0?30:0);prefs.edit().putBoolean("done_"+id,true).putInt("xp_"+id,Math.max(xp,prefs.getInt("xp_"+id,0))).putInt("bestMistakes_"+id,Math.min(mistakes,prefs.getInt("bestMistakes_"+id,Integer.MAX_VALUE))).apply();go("complete");}
    void showComplete(){LinearLayout r=split();addScene(r,"celebrate",0.47f);LinearLayout q=contentSide(r);q.addView(text("MISSION COMPLETE  /  BADGE UNLOCKED",13,TEAL));gap(q,12);q.addView(heading(mission().optString("badge"),40));gap(q,12);int xp=150+(mistakes==0?30:0);q.addView(text("+"+xp+" XP     •     "+(mistakes==0?"Perfect quest!":"5 / 5 challenges solved"),24,AMBER));gap(q,12);q.addView(text("100 XP for decisions + 50 XP for completion"+(mistakes==0?"\n+ 30 XP perfect bonus":"\n"+mistakes+" learning retries"),15,MUTED));gap(q,14);q.addView(text(mission().optString("takeaway"),18,WHITE));gap(q,12);q.addView(text("Your best score is saved. Replays improve your record; XP is counted once per mission.",13,MUTED));gap(q,16);addButton(q,"Explore another room   →",()->go("missions"));addButton(q,"Replay this mission",()->start(missionIndex));addButton(q,"My Progress",()->go("progress"));say("Mission complete. You earned the "+mission().optString("badge")+" badge and "+xp+" experience points. "+mission().optString("takeaway"));}
    void showProgress(){body.addView(text("YOUR HOME-SKILLS JOURNEY",13,TEAL));gap(body,10);body.addView(heading(totalXp()+" XP   /   "+completed()+" badges collected",35));gap(body,12);body.addView(text(completed()==3?"Every room explored. Replay a quest to chase a perfect score.":"Every small lesson builds a little more confidence.",19,MUTED));gap(body,25);for(int i=0;i<3;i++){final int index=i;JSONObject m=missions.optJSONObject(i);String id=m.optString("id");boolean done=prefs.getBoolean("done_"+id,false);addButton(body,(done?"✓  ":"○  ")+m.optString("badge")+"   /   "+m.optString("title")+"   •   "+prefs.getInt("xp_"+id,0)+" XP",()->start(index));}gap(body,14);addButton(body,"Return home",()->go("home"));say("Your progress. "+totalXp()+" experience points. "+completed()+" of three badges collected.");}
    void showHelp(){ScrollView scroll=new ScrollView(this);body.addView(scroll);LinearLayout q=column();scroll.addView(q);q.addView(heading("Small lessons. Real confidence.",34));gap(q,12);q.addView(text("Use the D-pad to move the bright focus ring. Press Select to choose. Back returns to the previous screen. During a mission, Back lets you continue or leave.",20,WHITE));gap(q,12);q.addView(text("Explore a room, answer five challenges, and solve a hands-on puzzle. Correct decisions earn 20 XP; completing a mission adds 50 XP; a perfect run adds 30 XP. Your best result is stored on this device.",18,MUTED));gap(q,12);q.addView(text("QuestGuide is a deterministic Smart Learning Guide with curated explanations and hints. It does not use live generative AI. FixQuest AI is an educational simulation, not a diagnostic or professional repair service.",18,MUTED));gap(q,16);addButton(q,"Narration: "+(narration?"ON":"OFF")+(speechReady?"":" • offline voice unavailable"),()->{narration=!narration;prefs.edit().putBoolean("narration",narration).apply();if(!narration&&speech!=null)speech.stop();render();});float rate=prefs.getFloat("rate",0.9f);addButton(q,"Speech pace: "+(rate<0.85f?"Relaxed":"Standard"),()->{float next=rate<0.85f?0.9f:0.75f;prefs.edit().putFloat("rate",next).apply();if(speechReady)speech.setSpeechRate(next);render();});addButton(q,"Back to the house",()->go("home"));say("How it works. Use the D-pad to move, Select to choose, and Back to return. QuestGuide provides curated offline learning guidance. This is an educational simulation.");}
    @Override public void onBackPressed(){if(screen.equals("play")){new AlertDialog.Builder(this).setTitle("Leave this quest?").setMessage("Completed mission records are saved. This attempt will restart if you leave.").setNegativeButton("Keep learning",(d,w)->{}).setPositiveButton("Choose a mission",(d,w)->go("missions")).show();}else if(screen.equals("home")){super.onBackPressed();}else if(screen.equals("intro")||screen.equals("complete")){go("missions");}else go("home");}
    @Override protected void onPause(){if(speech!=null)speech.stop();super.onPause();}
    @Override public void onWindowFocusChanged(boolean hasFocus){super.onWindowFocusChanged(hasFocus);if(hasFocus)getWindow().getDecorView().setSystemUiVisibility(5894);}
    @Override protected void onDestroy(){if(speech!=null){speech.stop();speech.shutdown();}super.onDestroy();}
}




