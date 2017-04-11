/** *************************************************************
 * file: Main.java
 * author: Jhuo Wei Ku
 * class: CS 445 – Computer Graphics
 *
 * assignment: Program 1
 * date last modified: 04/10/2017
 *
 * purpose: this program reads the coordinates in the coordinates.txt files and 
 * renders lines, circles, and ellipses with different color.
 *
 *
 *************************************************************** */
package cs445.p1;

import java.util.Scanner;
import java.io.File;
import java.util.Scanner;
import org.lwjgl.input.Keyboard;
import org.lwjgl.opengl.Display;
import org.lwjgl.opengl.DisplayMode;
import static org.lwjgl.opengl.GL11.*;
import static org.lwjgl.util.glu.GLU.*;

/**
 *
 * @author Joe Ku
 */
public class Main {

    public static void main(String[] args) {
        Main mn = new Main();
        mn.userUI();
    }
    
    private void userUI() {
        try {
            initializeWindow();
            render();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    //method:initializeWindow
    //purpose:this method initialize the window size, the background color,
    //the coordinates system and GL mode
    private void initializeWindow() throws Exception {
        Display.setFullscreen(false);
        Display.setDisplayMode(new DisplayMode(640, 480));
        Display.setTitle("Program 1");
        Display.create();

        glClearColor(0f, 0f, 0f, 0.0f);

        glMatrixMode(GL_PROJECTION);
        glLoadIdentity();

        glOrtho(0, 640, 0, 480, 1, -1);

        glMatrixMode(GL_MODELVIEW);
        glHint(GL_PERSPECTIVE_CORRECTION_HINT, GL_NICEST);
    }

    //method:render
    //purpose:this method reads the file and parse them into different values
    //that are passed in different draw methods based on the first letter
    //"l" for lines, "c" for circles, "e" for ellipses
    public void render() {
        while (!CloseRequested()) {
            try {
                glClear(GL_COLOR_BUFFER_BIT | GL_DEPTH_BUFFER_BIT);
                glLoadIdentity();

                try {
                    File file = new File("src/coordinates.txt");
                    Scanner sc = new Scanner(file);
                    while (sc.hasNextLine()) {
                        String input = sc.nextLine();
                        String[] splitString = input.split(" ");
                        char shapeType = splitString[0].charAt(0);
                        switch (shapeType) {
                            case 'l':
                                String[] point1 = splitString[1].split(",");
                                String[] point2 = splitString[2].split(",");
                                drawLine(Float.parseFloat(point1[0]), Float.parseFloat(point1[1]), Float.parseFloat(point2[0]), Float.parseFloat(point2[1]));
                                break;
                            case 'c':
                                String[] circleCenter = splitString[1].split(",");
                                drawCircle(Float.parseFloat(circleCenter[0]), Float.parseFloat(circleCenter[1]), Float.parseFloat(splitString[2]));
                                break;
                            case 'e':
                                String[] elipseCenter = splitString[1].split(",");
                                String[] elipseRadius = splitString[2].split(",");
                                drawEllipse(Float.parseFloat(elipseCenter[0]), Float.parseFloat(elipseCenter[1]),
                                        Float.parseFloat(elipseRadius[0]), Float.parseFloat(elipseRadius[1]));
                                break;
                            default:
                                break;
                        }

                    }
                    Display.update();
                    Display.sync(60);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        Display.destroy();
    }

    //method:drawEllipse
    //purpose:draw a simple ellipse
    private void drawEllipse(float cX, float cY, float rad1, float rad2) {
        //green
        glColor3f(0f, 1f, 0f);
        float outX, outY, theta;

        for (float angle = 0; angle <= 360; angle++) {
            theta = (float) ((angle * Math.PI) / 180);
            outX = (float) Math.cos(theta) * rad1 + cX;
            outY = (float) Math.sin(theta) * rad2 + cY;
            glBegin(GL_POINTS);
            glVertex2f(outX, outY);
            glEnd();
        }
    }

    //method:drawCircle
    //purpose:draw a simple circle
    private void drawCircle(float cX, float cY, float radius) {
        //blue
        glColor3f(0f, 0f, 1f);
        float outX, outY, theta;

        for (float angle = 0; angle <= 360; angle++) {
            theta = (float) ((angle * Math.PI) / 180);
            outX = (float) Math.cos(theta) * radius + cX;
            outY = (float) Math.sin(theta) * radius + cY;
            glBegin(GL_POINTS);
            glVertex2f(outX, outY);
            glEnd();
        }
    }

    //method:drawLine
    //purpose:this method calculates dx, dy, and the slope and pass in the coordinates
    //to draw lines in two different cases: -1 <slope<1 and others
    private void drawLine(float x1, float y1, float x2, float y2) {
        //red
        glColor3f(1f, 0f, 0f);
        float dx, dy, slope;

        //calculate dx, dy, and slope
        dx = x2 - x1;
        dy = y2 - y1;
        slope = dy / dx;

        if (slope > -1 && slope < 1) {
            regularDrawLine(x1, x2, y1, y2, slope, dx, dy);
        } else {
            steepDrawLine(x1, x2, y1, y2, slope, dx, dy);
        }
    }

    //method:regularDrawLine
    //purpose:draw regular lines that slope is <=1 and >=-1
    private void regularDrawLine(float x1, float x2, float y1, float y2, float slope, float dx, float dy) {
        float x, y, d, incrementRight, incrementUpRight;

        d = (float) (dy - 0.5 * dx);
        incrementRight = 2 * dy;
        incrementUpRight = 2 * (dy - dx);

        //x and y are temp pointers
        x = x1;
        y = y1;

        //draw first point
        glBegin(GL_POINTS);
        glVertex2f(x, y);
        glEnd();

        if (slope > 0) {
            //if x1 and y1 are on the left of x2 and y2
            if (dx > 0 && dy > 0) {
                //d = 2(dy)-dx
                d = (float) (dy - 0.5 * dx);
                while (x < x2) {
                    if (d > 0) {
                        //x+1, y+1
                        x = x + 1;
                        y = y + 1;
                        //draw new (x,y)
                        glBegin(GL_POINTS);
                        glVertex2f(x, y);
                        glEnd();
                        d = d + incrementUpRight;
                    } else {
                        //x+1
                        x = x + 1;
                        //draw new (x,y)
                        glBegin(GL_POINTS);
                        glVertex2f(x, y);
                        glEnd();
                        d = d + incrementRight;
                    }
                }
            } //if x1 and y1 are on the right of x2 and y2
            else if (dx < 0 && dy < 0) {
                //d = 0.5dx-dy
                d = (float) (0.5 * dx - dy);
                while (x > x2) {
                    if (d > 0) {
                        //x+1, y+1
                        x = x - 1;
                        y = y - 1;
                        //draw new (x,y)
                        glBegin(GL_POINTS);
                        glVertex2f(x, y);
                        glEnd();
                        d = d - (dy - dx);
                    } else {
                        //x+1
                        x = x - 1;
                        //draw new (x,y)
                        glBegin(GL_POINTS);
                        glVertex2f(x, y);
                        glEnd();
                        d = d - dy;
                    }
                }
            }
        } //for slope is negative
        else if (slope < 0) {
            //if x1 and y1 are on the right of x2 and y2
            if (dx < 0 && dy > 0) {
                dy = -dy;
                //d = 2(dy)-dx
                d = (float) (0.5 * dx - dy);
                while (x > x2) {
                    if (d > 0) {
                        //x+1, y+1
                        x = x - 1;
                        y = y + 1;
                        //draw new (x,y)
                        glBegin(GL_POINTS);
                        glVertex2f(x, y);
                        glEnd();
                        d = d - (dy - dx);
                    } else {
                        //x+1
                        x = x - 1;
                        //draw new (x,y)
                        glBegin(GL_POINTS);
                        glVertex2f(x, y);
                        glEnd();
                        d = d - dy;
                    }
                }

            } //if x1 and y1 are on the left of x2 and y2
            else if (dx > 0 && dy < 0) {
                dy = -dy;
                d = (float) (dy - 0.5 * dx);
                while (x < x2) {
                    if (d > 0) {
                        //x+1, y+1
                        x = x + 1;
                        y = y - 1;
                        //draw new (x,y)
                        glBegin(GL_POINTS);
                        glVertex2f(x, y);
                        glEnd();
                        d = d + dy - dx;
                    } else {
                        //x+1
                        x = x + 1;
                        //draw new (x,y)
                        glBegin(GL_POINTS);
                        glVertex2f(x, y);
                        glEnd();
                        d = d + dy;
                    }
                }
            }
        }

    }

    //method:steepDrawLine
    //purpose:draw regular lines that slope is >=1 and <=-1
    private void steepDrawLine(float x1, float x2, float y1, float y2, float slope, float dx, float dy) {

        float x, y, d, incrementRight, incrementUpRight;

        //switch dx and dy with regularDrawLine because the slope is too steep
        d = (float) ((0.5 * dy) - dx);
        incrementRight = 2 * dx;
        incrementUpRight = 2 * (dx - dy);

        //x and y are temp pointer
        x = x1;
        y = y1;

        //draw first point
        glBegin(GL_POINTS);
        glVertex2f(x, y);
        glEnd();

        if (slope > 0) {
            if (dy > 0 && dy > 0) {
                //d = (0.5 * dy)-dx
                d = (float) ((0.5 * dy) - dx);
                while (y < y2) {
                    if (d > 0) {
                        //x+1, y+1
                        x = x + 1;
                        y = y + 1;
                        //draw new (x,y)
                        glBegin(GL_POINTS);
                        glVertex2f(x, y);
                        glEnd();
                        d = 2 * (dx - dy) + d;
                    } else {
                        //x+1
                        y = y + 1;
                        //draw new (x,y)
                        glBegin(GL_POINTS);
                        glVertex2f(x, y);
                        glEnd();
                        d = d + 2 * dx;
                    }
                }
            } else if (dy < 0 && dx < 0) {
                dy = -dy;
                dx = -dx;
                while (y > y2) {
                    if (d > 0) {
                        //x+1, y+1
                        x = x - 1;
                        y = y - 1;
                        //draw new (x,y)
                        glBegin(GL_POINTS);
                        glVertex2f(x, y);
                        glEnd();
                        d = 2 * (dx - dy) + d;
                    } else {
                        //x+1
                        y = y - 1;
                        //draw new (x,y)
                        glBegin(GL_POINTS);
                        glVertex2f(x, y);
                        glEnd();
                        d = d + 2 * dx;
                    }
                }

            }
        } else if (slope < 0) {
            if (dy > 0 && dx < 0) {
                dx = -dx;
                while (y < y2) {
                    if (d > 0) {
                        //x+1, y+1
                        x = x - 1;
                        y = y + 1;
                        //draw new (x,y)
                        glBegin(GL_POINTS);
                        glVertex2f(x, y);
                        glEnd();
                        d = 2 * (dx - dy) + d;
                    } else {
                        //x+1
                        y = y + 1;
                        //draw new (x,y)
                        glBegin(GL_POINTS);
                        glVertex2f(x, y);
                        glEnd();
                        d = d + 2 * dx;
                    }
                }

            } else if (dx > 0 && dy < 0) {
                dy = -dy;
                while (y > y2) {
                    if (d > 0) {
                        //x+1, y+1
                        x = x + 1;
                        y = y - 1;
                        //draw new (x,y)
                        glBegin(GL_POINTS);
                        glVertex2f(x, y);
                        glEnd();
                        d = 2 * (dx - dy) + d;
                    } else {
                        //x+1
                        y = y - 1;
                        //draw new (x,y)
                        glBegin(GL_POINTS);
                        glVertex2f(x, y);
                        glEnd();
                        d = d + 2 * dx;
                    }
                }

            }
        }
    }

    //method:CloseRequested
    //purpose:return true if windows' close is clicked
    //or the espace key is pressed
    private boolean CloseRequested() {
        boolean result = false;

        if (Display.isCloseRequested()
                || Keyboard.isKeyDown(Keyboard.KEY_ESCAPE)) {
            result = true;
        }
        return result;
    }
}
