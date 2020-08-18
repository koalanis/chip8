import org.koala.bitlogic.BinaryLogic;
import org.koala.emu.chip8.CPU;
import org.koala.emu.chip8.Emulator;

import javax.swing.*;
import java.awt.*;
import java.io.*;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Arrays;
import java.util.Scanner;

/**
 * Created by kaleb on 12/31/2016.
 */
public class Main extends JFrame{

    public Main() throws InterruptedException, IOException {

        System.out.println("Hello World !!!!!");

        String workingdir = System.getProperty("user.dir");


        String dirName = "\\res\\c8games\\";
        String romName = "PONG";

        // This will reference one line at a time
        String line = null;

        Path path = Paths.get(workingdir+dirName+romName);
        byte[] bin = Files.readAllBytes(path);
        System.out.println(Arrays.toString(bin));
        System.out.println(bin.length);





        String program = Emulator.KALEID;
        byte[] bp = BinaryLogic.hexStringToByteArray(program);
        System.out.println(bp.length);
        System.out.println(program.length());



        if(program.length() != 0) {

            CPUCanvas canvas = new CPUCanvas(new CPU(bin));
            addKeyListener(canvas.cpu);
            setLayout(new BorderLayout());
            setSize(500,300);
            setTitle("Chip 8");
            setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
            this.add("Center", canvas);
            setLocationRelativeTo(null);
            setVisible(true);

            canvas.cpu.initialize();



            long startTime = System.currentTimeMillis();
            int target = 5000;
            for(;;) {
                canvas.cpu.emulateCycle();
                if(canvas.cpu.drawFlag) {
                    canvas.repaint();
                }
                if(canvas.cpu.numberOfOps % target == target-1) {
//                    System.out.println(canvas.cpu.numberOfOps);
                }
            }
        }

    }


    public static void main(String[] args) throws InterruptedException, IOException {
        new Main();
    }
}
