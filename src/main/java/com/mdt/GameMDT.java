package com.mdt;
import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.util.*;

public class GameMDT extends JPanel implements ActionListener {
    enum State { BOOT, MENU, SELECT, CREATE, OPTIONS, LOADING, INTRO, FIGHT, PAUSE, POST }
    State state = State.BOOT;
    double timer = 5.0;
    javax.swing.Timer loop = new javax.swing.Timer(16, this);

    static class Fighter {
        String name, weight, style, country; int ovr; Color skin, hair; boolean beard, bald;
        Fighter(String n, int o, String w, String s, String c, Color sk, Color hr, boolean b, boolean bl) {
            name=n; ovr=o; weight=w; style=s; country=c; skin=sk; hair=hr; beard=b; bald=bl;
        }
    }
    java.util.List<Fighter> heavy = new ArrayList<>(), light = new ArrayList<>(), roster = new ArrayList<>();
    int pIdx = 0, bIdx = 1, modeIdx = 0, diffIdx = 1, arenaIdx = 0, wFilter = 2, shortsIdx = 1, gfxIdx = 2, vol = 85, coins = 500;
    String[] modes = {"Champion Belt", "Normal", "Streak"}, diffs = {"Easy", "Medium", "HARD"}, arenas = {"O2", "PRIME", "Pringles"}, filters = {"Heavy", "Light", "All"}, shorts = {"Black", "Red", "Green", "Gold", "Blue"};
    Color[] shortsCols = {Color.DARK_GRAY, new Color(215,20,30), new Color(30,160,60), new Color(230,185,30), new Color(30,90,220)};
    boolean blood = true, typingCheat = false, guarding = false, dodging = false, inSub = false;
    String cheat = "", cName = "Custom Fighter", resText = "", notif = "";
    int cOvr = 87; String cWeight = "Heavy"; double notifT = 0;

    double pHP = 100, bHP = 100, pStam = 100, rTime = 300, infraT = 0, slowMoT = 0, subProg = 0, punchT = 0, botPunchT = 0;
    int round = 1, pX = 420, bX = 760;
    java.util.List<int[]> bloodPts = new ArrayList<>();

    public GameMDT() {
        Color lSk = new Color(235,188,155), tSk = new Color(198,145,108), dSk = new Color(105,65,45);
        Color blH = new Color(25,25,25), brH = new Color(85,55,35), ylH = new Color(220,185,80);
        heavy.add(new Fighter("Dominik Szofer", 89, "Heavy", "Brawler", "Poland", lSk, brH, true, false));
        heavy.add(new Fighter("Jon Jones", 88, "Heavy", "MMA", "USA", dSk, blH, true, true));
        heavy.add(new Fighter("Tom Aspinall", 88, "Heavy", "Boxer", "UK", lSk, brH, true, false));
        heavy.add(new Fighter("Jan Blachowicz", 87, "Heavy", "Kickboxer", "Poland", lSk, brH, true, false));
        heavy.add(new Fighter("Brock Lesnar", 85, "Heavy", "Wrestler", "USA", lSk, ylH, true, false));
        heavy.add(new Fighter("Frank Mir", 85, "Heavy", "Jiu-Jitsu", "USA", tSk, blH, true, false));
        heavy.add(new Fighter("The Rock", 85, "Heavy", "Powerhouse", "USA", tSk, blH, false, true));
        heavy.add(new Fighter("Thiago Silva", 84, "Heavy", "Muay Thai", "Brazil", tSk, blH, true, true));
        heavy.add(new Fighter("Francis Ngannou", 83, "Heavy", "Slugger", "Cameroon", dSk, blH, true, false));
        heavy.add(new Fighter("Alex Pereira", 83, "Heavy", "Kickboxer", "Brazil", tSk, blH, false, true));

        light.add(new Fighter("Islam Makhachev", 89, "Light", "Sambo", "Russia", lSk, blH, true, false));
        light.add(new Fighter("Charles Oliveira", 88, "Light", "Jiu-Jitsu", "Brazil", tSk, ylH, true, false));
        light.add(new Fighter("Zaldyrek", 87, "Light", "Striker", "Poland", lSk, brH, false, false));
        light.add(new Fighter("CM Punk", 86, "Light", "Brawler", "USA", lSk, blH, true, false));
        light.add(new Fighter("Ben Askren", 86, "Light", "Wrestler", "USA", lSk, brH, false, false));
        light.add(new Fighter("Mateusz Gamrot", 85, "Light", "Wrestler", "Poland", lSk, brH, true, false));
        light.add(new Fighter("Conor McGregor", 85, "Light", "Boxer", "Ireland", lSk, brH, true, false));
        light.add(new Fighter("Khabib Nurmagomedov", 84, "Light", "Grappler", "Russia", lSk, blH, true, false));
        light.add(new Fighter("Anderson Silva", 84, "Light", "Muay Thai", "Brazil", dSk, blH, false, true));
        light.add(new Fighter("Max Holloway", 83, "Light", "Boxer", "USA", tSk, blH, false, false));
        updateRoster();

        setFocusable(true);
        addKeyListener(new KeyAdapter() {
            public void keyReleased(KeyEvent e) { if (e.getKeyCode() == KeyEvent.VK_G) guarding = false; }
            public void keyTyped(KeyEvent e) {
                char c = e.getKeyChar();
                if (Character.isISOControl(c)) return;
                if (state == State.OPTIONS && typingCheat) cheat += c;
                else if (state == State.CREATE && cName.length() < 18) cName += c;
            }
            public void keyPressed(KeyEvent e) { handleKey(e.getKeyCode()); }
        });
        addMouseListener(new MouseAdapter() {
            public void mousePressed(MouseEvent e) {
                int x = e.getX(), y = e.getY();
                if (state == State.MENU && x >= 70 && x <= 370) {
                    if (y >= 210 && y <= 265) state = State.SELECT;
                    else if (y >= 285 && y <= 340) state = State.CREATE;
                    else if (y >= 360 && y <= 415) state = State.OPTIONS;
                    else if (y >= 435 && y <= 490) System.exit(0);
                } else if (state == State.SELECT && x >= 880 && x <= 1210 && y >= 590 && y <= 655) startFight();
            }
        });
        loop.start();
    }

    void updateRoster() {
        roster.clear();
        if (wFilter == 0 || wFilter == 2) roster.addAll(heavy);
        if (wFilter == 1 || wFilter == 2) roster.addAll(light);
        pIdx = 0; bIdx = Math.min(1, roster.size() - 1);
    }

    void startFight() { bIdx = (pIdx + 1) % roster.size(); state = State.LOADING; timer = 2.5; }
    void notify(String s) { notif = s; notifT = 3.0; }

    void handleKey(int k) {
        if (k == KeyEvent.VK_BACK_SPACE) {
            if (state == State.OPTIONS && typingCheat && cheat.length() > 0) cheat = cheat.substring(0, cheat.length()-1);
            if (state == State.CREATE && cName.length() > 0) cName = cName.substring(0, cName.length()-1);
        }
        if (state == State.MENU) {
            if (k == KeyEvent.VK_1 || k == KeyEvent.VK_ENTER) state = State.SELECT;
            if (k == KeyEvent.VK_2) state = State.CREATE;
            if (k == KeyEvent.VK_3) state = State.OPTIONS;
            if (k == KeyEvent.VK_4 || k == KeyEvent.VK_ESCAPE) System.exit(0);
        } else if (state == State.SELECT) {
            if (k == KeyEvent.VK_DOWN) pIdx = (pIdx + 1) % roster.size();
            if (k == KeyEvent.VK_UP) pIdx = (pIdx - 1 + roster.size()) % roster.size();
            if (k == KeyEvent.VK_W) { wFilter = (wFilter + 1) % 3; updateRoster(); }
            if (k == KeyEvent.VK_M) modeIdx = (modeIdx + 1) % 3;
            if (k == KeyEvent.VK_D) diffIdx = (diffIdx + 1) % 3;
            if (k == KeyEvent.VK_A) arenaIdx = (arenaIdx + 1) % 3;
            if (k == KeyEvent.VK_B) blood = !blood;
            if (k == KeyEvent.VK_C) shortsIdx = (shortsIdx + 1) % 5;
            if (k == KeyEvent.VK_ENTER) startFight();
            if (k == KeyEvent.VK_ESCAPE) state = State.MENU;
        } else if (state == State.CREATE) {
            if (k == KeyEvent.VK_UP && cOvr < 99) cOvr++;
            if (k == KeyEvent.VK_DOWN && cOvr > 70) cOvr--;
            if (k == KeyEvent.VK_TAB) cWeight = cWeight.equals("Heavy") ? "Light" : "Heavy";
            if (k == KeyEvent.VK_ENTER) {
                Fighter f = new Fighter(cName, cOvr, cWeight, "Striker", "Poland", new Color(230,185,150), Color.DARK_GRAY, true, false);
                if (cWeight.equals("Heavy")) heavy.add(0, f); else light.add(0, f);
                updateRoster(); notify("CREATED: " + cName); state = State.MENU;
            }
            if (k == KeyEvent.VK_ESCAPE) state = State.MENU;
        } else if (state == State.OPTIONS) {
            if (k == KeyEvent.VK_TAB) typingCheat = !typingCheat;
            else if (k == KeyEvent.VK_ENTER && typingCheat) {
                if (cheat.equals("get-coins")) { coins += 1000; notify("+1000 COINS!"); }
                else if (cheat.equals("instant-win")) { bHP = 0; notify("INSTANT WIN READY!"); }
                else if (cheat.equals("quit-win")) { resText = "WALKOVER WIN (QUIT-WIN)!"; state = State.POST; }
                else notify("UNKNOWN CODE");
                cheat = ""; typingCheat = false;
            } else if (!typingCheat) {
                if (k == KeyEvent.VK_G) gfxIdx = (gfxIdx + 1) % 3;
                if (k == KeyEvent.VK_RIGHT && vol < 100) vol += 10;
                if (k == KeyEvent.VK_LEFT && vol > 0) vol -= 10;
                if (k == KeyEvent.VK_B) blood = !blood;
                if (k == KeyEvent.VK_ESCAPE) state = State.MENU;
            }
        } else if (state == State.FIGHT) {
            if (k == KeyEvent.VK_ESCAPE) { state = State.PAUSE; return; }
            if (slowMoT > 0) return;
            if (k == KeyEvent.VK_G) guarding = true;
            if (k == KeyEvent.VK_A || k == KeyEvent.VK_LEFT) pX = Math.max(220, pX - 35);
            if (k == KeyEvent.VK_D || k == KeyEvent.VK_RIGHT) pX = Math.min(bX - 95, pX + 35);
            if (k == KeyEvent.VK_SPACE && pStam >= 12) { dodging = true; pStam -= 12; return; }
            if (k == KeyEvent.VK_R) {
                inSub = true; subProg += 16;
                if (subProg >= 100) { resText = "SUBMISSION VICTORY (TAP OUT)!"; slowMoT = 2.2; }
                return;
            }
            double dmg = 0; boolean head = false;
            if (k == KeyEvent.VK_J && pStam >= 8) { dmg = 7; punchT = 0.25; pStam -= 8; }
            if (k == KeyEvent.VK_K && pStam >= 12) { dmg = 12; punchT = 0.3; pStam -= 12; }
            if (k == KeyEvent.VK_L && pStam >= 18) { dmg = 18; punchT = 0.35; head = true; pStam -= 18; }

if (k == KeyEvent.VK_I && pStam >= 22) { dmg = 25; punchT = 0.4; head = true; pStam -= 22; }
if (dmg > 0 && Math.abs(pX - bX) < 165) {
bHP = Math.max(0, bHP - dmg);
if (blood && head) addBlood(bX + 30, 310);
if (bHP <= 0) { resText = "KNOCKOUT (KO)!"; slowMoT = 2.5; }
}
if (!dodging && bHP > 0 && Math.random() < 0.45) {
botPunchT = 0.3;
double bd = (diffIdx == 2 ? 18 : (diffIdx == 1 ? 12 : 6)) * (guarding ? 0.25 : 1.0);
pHP = Math.max(0, pHP - bd);
if (bd >= 15) { infraT = 2.0; if (blood) addBlood(pX + 30, 310); }
if (pHP <= 0) { resText = "YOU WERE KNOCKED OUT!"; slowMoT = 2.5; }
}
} else if (state == State.PAUSE) {
if (k == KeyEvent.VK_ESCAPE || k == KeyEvent.VK_1) state = State.FIGHT;
if (k == KeyEvent.VK_2) state = State.MENU;
} else if (state == State.POST && (k == KeyEvent.VK_ENTER || k == KeyEvent.VK_ESCAPE)) state = State.MENU;
}
void addBlood(int x, int y) {
Random r = new Random();
for (int i = 0; i < 16; i++) bloodPts.add(new int[]{x, y, r.nextInt(14)-7, -r.nextInt(10), 40});
}
public void actionPerformed(ActionEvent e) {
double dt = 0.016;
if (slowMoT > 0) { slowMoT -= dt; dt *= 0.25; if (slowMoT <= 0) state = State.POST; }
if (notifT > 0) notifT -= dt;
if (infraT > 0) infraT -= dt;
if (punchT > 0) punchT -= dt;
if (botPunchT > 0) botPunchT -= dt;
if (dodging && Math.random() < 0.08) dodging = false;
bloodPts.removeIf(p -> { p[0]+=p[2]; p[1]+=p[3]; p[3]+=1; p[4]--; return p[4]<=0; });
if (state == State.BOOT) { timer -= dt; if (timer <= 0) state = State.MENU; }
else if (state == State.LOADING) {
timer -= dt;
if (timer <= 0) {
pHP=100; bHP=100; pStam=100; round=1; rTime=300; pX=450; bX=730; infraT=0; slowMoT=0; subProg=0; inSub=false;
state = State.INTRO; timer = 3.2;
}
} else if (state == State.INTRO) { timer -= dt; if (timer <= 0) state = State.FIGHT; }
else if (state == State.FIGHT && slowMoT <= 0) {
pStam = Math.min(100, pStam + dt * 9);
rTime -= dt;
if (rTime <= 0) {
if (round < 3) { round++; rTime = 300; notify("ROUND " + round); }
else { resText = pHP >= bHP ? "DECISION VICTORY!" : "DECISION DEFEAT!"; state = State.POST; }
}
if (inSub) { subProg = Math.max(0, subProg - dt * 11); if (subProg == 0) inSub = false; }
}
repaint();
}
protected void paintComponent(Graphics g0) {
super.paintComponent(g0);
Graphics2D g = (Graphics2D) g0;
g.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
g.setColor(new Color(18, 18, 22));
g.fillRect(0, 0, 1280, 720);
if (state == State.MENU || state == State.INTRO || state == State.FIGHT || state == State.PAUSE) {
drawOctagon(g);
}
Fighter f1 = roster.get(pIdx), f2 = roster.get(bIdx);
g.setFont(new Font("Arial", Font.BOLD, 22));
if (state == State.BOOT) {
g.setColor(new Color(215, 20, 30)); g.fillRoundRect(390, 230, 500, 240, 25, 25);
g.setColor(Color.WHITE); g.fillRoundRect(400, 240, 480, 220, 20, 20);
g.setColor(new Color(215, 20, 30)); g.setFont(new Font("Impact", Font.ITALIC, 110));
g.drawString("MDT", 535, 365);
g.setFont(new Font("Arial", Font.BOLD, 18)); g.setColor(Color.DARK_GRAY);
g.drawString("LOADING MMA ENGINE... " + String.format("%.1fs", timer), 515, 420);
} else if (state == State.MENU) {
g.setColor(new Color(245, 245, 248)); g.fillRoundRect(45, 35, 350, 645, 20, 20);
g.setColor(new Color(215, 20, 30)); g.fillRoundRect(45, 35, 350, 135, 20, 20);
g.setColor(Color.WHITE); g.setFont(new Font("Impact", Font.ITALIC, 78)); g.drawString("MDT", 80, 125);
g.setFont(new Font("Arial", Font.BOLD, 20)); g.drawString("MMA 3D EDITION", 225, 120);
drawBtn(g, 70, 210, 300, 55, "1. START FIGHT", true);
drawBtn(g, 70, 285, 300, 55, "2. CREATE FIGHTER", true);
drawBtn(g, 70, 360, 300, 55, "3. OPTIONS & CHEATS", true);
drawBtn(g, 70, 435, 300, 55, "4. EXIT", false);
g.setColor(new Color(215, 20, 30)); g.setFont(new Font("Arial", Font.BOLD, 20));
g.drawString("COINS: " + coins, 75, 555);
g.setColor(Color.GRAY); g.drawString("(C) 2020 MDT, INC.", 75, 635);
} else if (state == State.SELECT) {
g.setColor(new Color(245,245,248)); g.fillRoundRect(35, 25, 1210, 665, 20, 20);
g.setColor(new Color(215,20,30)); g.fillRoundRect(35, 25, 1210, 65, 20, 20);
g.setColor(Color.WHITE); g.drawString("SELECT FIGHTER (1P VS BOT) - USE UP/DOWN ARROWS", 60, 65);
for (int i = 0; i < Math.min(12, roster.size()); i++) {
Fighter f = roster.get(i); boolean sel = (i == pIdx);
g.setColor(sel ? new Color(215,20,30) : new Color(225,225,230));
g.fillRoundRect(60, 110 + i * 45, 530, 38, 10, 10);
g.setColor(sel ? Color.WHITE : Color.BLACK);
g.drawString(f.name + " (" + f.ovr + " OVR) - " + f.weight + " [" + f.style + "]", 75, 136 + i * 45);
}
g.setColor(new Color(215,20,30)); g.drawString("MATCH SETTINGS:", 640, 135);
g.setColor(Color.BLACK);
g.drawString("[W] WEIGHT: < " + filters[wFilter] + " >", 640, 190);
g.drawString("[M] MODE: < " + modes[modeIdx] + " >", 640, 240);
g.drawString("[D] DIFFICULTY: < " + diffs[diffIdx] + " >", 640, 290);
g.drawString("[A] ARENA: < " + arenas[arenaIdx] + " >", 640, 340);
g.drawString("[B] BLOOD: < " + (blood ? "YES" : "NO") + " >", 640, 390);
g.drawString("[C] SHORTS: < " + shorts[shortsIdx] + " >", 640, 440);
drawBtn(g, 880, 590, 330, 65, "START FIGHT [ENTER]", true);
} else if (state == State.CREATE) {
g.setColor(new Color(245,245,248)); g.fillRoundRect(240, 100, 800, 500, 20, 20);
g.setColor(new Color(215,20,30)); g.fillRoundRect(240, 100, 800, 65, 20, 20);
g.setColor(Color.WHITE); g.drawString("CREATE CUSTOM FIGHTER", 270, 140);
g.setColor(Color.BLACK);
g.drawString("NAME (TYPE): " + cName + "", 280, 220);
g.drawString("OVERALL [UP/DOWN]: " + cOvr + " OVR", 280, 280);
g.drawString("WEIGHT [TAB]: " + cWeight, 280, 340);
drawBtn(g, 280, 440, 330, 60, "SAVE FIGHTER [ENTER]", true);
} else if (state == State.OPTIONS) {
g.setColor(new Color(245,245,248)); g.fillRoundRect(240, 100, 800, 500, 20, 20);
g.setColor(new Color(215,20,30)); g.fillRoundRect(240, 100, 800, 65, 20, 20);
g.setColor(Color.WHITE); g.drawString("OPTIONS & CHEAT CONSOLE", 270, 140);
g.setColor(Color.BLACK);
g.drawString("[LEFT/RIGHT] SOUND VOLUME: " + vol + "%", 280, 220);
g.drawString("[B] BLOOD EFFECTS: " + (blood ? "YES" : "NO"), 280, 280);
g.setColor(new Color(215,20,30));
g.drawString("[TAB] CHEAT INPUT: " + cheat + (typingCheat ? "" : " (PRESS TAB)"), 280, 350);
g.setColor(Color.GRAY); g.drawString("CODES: get-coins | instant-win | quit-win", 280, 400);
g.setColor(Color.BLACK); g.drawString("[ESC] BACK TO MENU", 280, 520);
} else if (state == State.LOADING) {
g.setColor(Color.WHITE); g.fillRoundRect(300, 210, 680, 280, 20, 20);
g.setColor(new Color(215,20,30)); g.drawString("MDT GALA - " + arenas[arenaIdx] + " ARENA", 470, 260);
g.setColor(Color.BLACK); g.drawString(f1.name + " (" + f1.ovr + ")   VS   " + f2.name + " (" + f2.ovr + ")", 400, 350);
g.setColor(Color.GRAY); g.drawString("LOADING OCTAGON...", 530, 430);
} else if (state == State.INTRO) {
g.setColor(new Color(215,20,30)); g.fillRoundRect(340, 50, 600, 75, 15, 15);
g.setColor(Color.WHITE); g.setFont(new Font("Impact", Font.ITALIC, 42));
g.drawString("READY TO FIGHT... FIGHT!", 435, 103);
} else if (state == State.FIGHT) {
g.setColor(Color.DARK_GRAY); g.fillRect(40, 25, 460, 42); g.fillRect(780, 25, 460, 42);
g.setColor(new Color(215,20,30));
g.fillRect(45, 30, (int)(4.5 * pHP), 22); g.fillRect(785, 30, (int)(4.5 * bHP), 22);
g.setColor(new Color(40,170,240)); g.fillRect(45, 55, (int)(4.5 * pStam), 8);
g.setColor(Color.WHITE);
g.drawString(f1.name + " (" + (int)pHP + " HP)", 45, 92);
g.drawString(f2.name + " (" + (int)bHP + " HP)", 785, 92);
g.setColor(Color.WHITE); g.fillRoundRect(560, 22, 160, 55, 10, 10);
g.setColor(new Color(215,20,30)); g.drawString("R" + round + "/3", 615, 45);
g.setColor(Color.BLACK); g.drawString(String.format("%02d:%02d", (int)rTime/60, (int)rTime%60), 610, 70);
if (inSub) {
g.setColor(Color.BLACK); g.fillRect(430, 580, 420, 35);
g.setColor(Color.ORANGE); g.fillRect(435, 585, (int)(4.1*subProg), 25);
g.setColor(Color.WHITE); g.drawString("MASH [R] FOR SUBMISSION TAP OUT!", 465, 570);
}
g.setFont(new Font("Arial", Font.BOLD, 16)); g.setColor(Color.LIGHT_GRAY);
g.drawString("[A/D] Move | [J/K/L/I] Strikes | [G] Guard | [SPACE] Dodge | [R] Submission | [ESC] Pause", 260, 695);
} else if (state == State.PAUSE) {
g.setColor(Color.WHITE); g.fillRoundRect(420, 210, 440, 260, 20, 20);
g.setColor(new Color(215,20,30)); g.drawString("GAME PAUSED", 560, 260);
drawBtn(g, 480, 300, 320, 50, "1. RESUME [ESC]", true);
drawBtn(g, 480, 370, 320, 50, "2. QUIT TO MENU", false);
} else if (state == State.POST) {
g.setColor(Color.WHITE); g.fillRoundRect(340, 220, 600, 240, 20, 20);
g.setColor(new Color(215,20,30)); g.setFont(new Font("Impact", Font.PLAIN, 40));
g.drawString(resText, 440, 315);
g.setFont(new Font("Arial", Font.BOLD, 20)); g.setColor(Color.BLACK);
g.drawString("PRESS [ENTER] TO CONTINUE", 495, 400);
}
if (notifT > 0) {
g.setColor(new Color(215,20,30)); g.fillRoundRect(440, 10, 400, 35, 10, 10);
g.setColor(Color.WHITE); g.drawString(notif, 520, 34);
}
}
void drawOctagon(Graphics2D g) {
GradientPaint gp = new GradientPaint(640, 0, arenaIdx==0 ? new Color(60,65,85) : (arenaIdx==1 ? new Color(20,55,110) : new Color(110,30,30)), 640, 500, new Color(15,15,20));
g.setPaint(gp); g.fillRect(0, 0, 1280, 720);
g.setColor(new Color(225, 225, 230));
g.fillPolygon(new int[]{240, 1040, 1160, 120}, new int[]{430, 430, 620, 620}, 4);
g.setColor(new Color(215, 20, 30));
g.fillOval(540, 485, 200, 75);
g.setColor(Color.WHITE); g.setFont(new Font("Impact", Font.ITALIC, 36)); g.drawString("MDT", 608, 536);
// Siatka i slupki
g.setStroke(new BasicStroke(6)); g.setColor(new Color(215, 20, 30));
g.drawRect(240, 230, 800, 200);
g.fillRect(232, 225, 16, 210); g.fillRect(1032, 225, 16, 210);
if (state == State.INTRO) {
// Sedzia z pasem
g.setColor(Color.DARK_GRAY); g.fillRoundRect(615, 310, 50, 130, 15, 15);
g.setColor(new Color(235,188,155)); g.fillOval(622, 270, 36, 38);
g.setColor(new Color(245, 200, 30)); g.fillRoundRect(590, 235, 100, 24, 8, 8);
}
Fighter f1 = roster.get(pIdx), f2 = roster.get(bIdx);
int x1 = (state == State.MENU) ? 540 : pX;
int x2 = (state == State.MENU) ? 690 : bX;
drawFighter(g, f1, x1, 290, true, shortsCols[shortsIdx], dodging, guarding, punchT > 0);
drawFighter(g, f2, x2, 290, false, Color.BLACK, false, false, botPunchT > 0);
g.setColor(new Color(210, 10, 10));
for (int[] p : bloodPts) g.fillOval(p[0], p[1], 8, 8);
if (infraT > 0) {
g.setColor(new Color(230, 10, 10, 115));
g.fillRect(0, 0, 1280, 720);
}
}
void drawFighter(Graphics2D g, Fighter f, int x, int y, boolean right, Color sh, boolean dodge, boolean guard, boolean punch) {
int dx = dodge ? (right ? -25 : 25) : 0;
int ext = punch ? (right ? 45 : -45) : 0;
// Nogi i spodenki
g.setColor(f.skin); g.fillRect(x+12+dx, y+130, 16, 75); g.fillRect(x+38+dx, y+130, 16, 75);
g.setColor(sh); g.fillRoundRect(x+8+dx, y+105, 50, 42, 10, 10);
// Tors
g.setColor(f.skin); g.fillRoundRect(x+6+dx, y+42, 54, 70, 16, 16);
// Glowa, wlosy, broda
g.fillOval(x+14+dx, y, 38, 42);
if (!f.bald) { g.setColor(f.hair); g.fillArc(x+14+dx, y-2, 38, 22, 0, 180); }
if (f.beard) { g.setColor(f.hair); g.fillArc(x+16+dx, y+20, 34, 22, 180, 180); }
// Rekawice
int gy = guard ? y + 18 : y + 55;
g.setColor(right ? new Color(215,20,30) : new Color(30,90,220));
g.fillOval(x + (right ? 48 : -8) + dx + ext, gy, 24, 22);
}
void drawBtn(Graphics2D g, int x, int y, int w, int h, String t, boolean red) {
g.setColor(red ? new Color(215,20,30) : new Color(55,55,60));
g.fillRoundRect(x, y, w, h, 14, 14);
g.setColor(Color.WHITE); g.setFont(new Font("Arial", Font.BOLD, 20));
g.drawString(t, x + 22, y + 35);
}
public static void main(String[] args) {
JFrame f = new JFrame("GRA MDT - MMA EDITION");
f.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
f.setSize(1296, 758);
f.setResizable(false);
f.setLocationRelativeTo(null);
f.add(new GameMDT());
f.setVisible(true);
}
}
