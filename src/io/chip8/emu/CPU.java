package io.chip8.emu;

import io.chip8.bitlogic.BinaryLogic;

import java.awt.*;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.util.Arrays;
import java.util.Random;

/**
 * Created by kaleb on 1/3/2017.
 */
public class CPU implements KeyListener{


    public boolean drawFlag;
    private boolean halt;
    private int lastKey;
    private byte[] programBuffer;
    public int numberOfOps;

    enum Registers {
        R_0(0),
        R_1(1),
        R_2(2),
        R_3(3),
        R_4(4),
        R_5(5),
        R_6(6),
        R_7(7),
        R_8(8),
        R_9(9),
        R_A(10),
        R_B(11),
        R_C(12),
        R_D(13),
        R_E(14),
        R_F(15),
        NUM_OF_REGISTERS(16);

        private final int value;
        private Registers(int value) {
            this.value = value;
        }

        public int getValue()
        {
            return value;
        }
    };

    public static final int WIDTH = 64;
    public static final int HEIGHT = 32;


    private byte V[] = new byte[Registers.NUM_OF_REGISTERS.getValue()];

    private short I;
    private short pc;

    private byte gfx[] = new byte[WIDTH * HEIGHT];

    private byte delayTimer;
    private byte soundTimer;

    private static final int STACK_LEVELS = 16;

    private short stack[] = new short[STACK_LEVELS];
    private short sp;

    private final Random random = new Random();

    private static final int NUM_KEYS = 16;
    private byte key[] = new byte[NUM_KEYS];

    private static final int MEMORY_SIZE = 4096;
    private byte memory[] = new byte[MEMORY_SIZE];

    private short opcode;

    private String chip8FontsetData  =  "F0909090F0"+ // 0
                                        "2060202070"+ // 1
                                        "F010F080F0"+ // 2
                                        "F010F010F0"+ // 3
                                        "9090F01010"+ // 4
                                        "F080F010F0"+ // 5
                                        "F080F090F0"+ // 6
                                        "F010204040"+ // 7
                                        "F090F090F0"+ // 8
                                        "F090F010F0"+ // 9
                                        "F090F09090"+ // A
                                        "E090E090E0"+ // B
                                        "F0808080F0"+ // C
                                        "E0909090E0"+ // D
                                        "F080F080F0"+ // E
                                        "F080F08080"; // F

    private byte[] chip8Fontset = BinaryLogic.hexStringToByteArray(chip8FontsetData);

    public CPU(String rom) {
        programBuffer = BinaryLogic.hexStringToByteArray(rom);
    }

    public CPU(byte[] rom) {
        programBuffer = rom;
    }

    private void clearGFX() {
        for (int i = 0; i < gfx.length; i++) {
            gfx[i] = 0;
        }
    }

    private void clearStack() {
        for (int i = 0; i < stack.length; i++) {
            stack[i] = 0;
        }
    }

    private void clearRegisters() {
        for (int i = 0; i < V.length; i++) {
            V[i] = 0;
        }
    }

    private void clearMemory() {
        for (int i = 0; i < memory.length; i++) {
            memory[i] = 0;
        }
    }

    public void reset() {
        clearGFX();
        clearMemory();
        clearRegisters();
        clearStack();
    }


    public void initialize() {
        pc = 0x200;
        sp = 0;
        I = 0;
        opcode = 0;
        
        // Clear display	
        // Clear stack
        // Clear registers V0-VF
        // Clear memory
        reset();

        // Load fontset
        for (int i = 0; i < 0x50; i++) {
            memory[i] = chip8Fontset[i];
        }

        for (int i = 0; i < programBuffer.length; i++) {
            memory[i+0x200] =  programBuffer[i];
        }
    }

    public void clearKeys() {
        for (int i = 0; i < key.length; i++) {
            key[i] = 0;
        }
    }

    public void emulateCycle() {
        // Fetch Opcode
        drawFlag = false;
//        System.out.println("DrawFlag OFF");


        if(halt)
            System.out.println("HALT");

        if(!halt) {
            opcode = BinaryLogic.twoBytesToShort(memory[pc], memory[pc+1]);
            // Decode Opcode & Execute Opcode
            decodeAndExecute(opcode);
            // Update timers
        }


        if(delayTimer > 0) {
            --delayTimer;
        }

        if(soundTimer > 0) {
            if(soundTimer == 1) {
                System.out.println("BEEEP");
            }
            --soundTimer;
        }
    }

    public int getPixelAt(int x, int y) {
        return gfx[x+y*CPU.WIDTH];
    }

    private void decodeAndExecute(short opcode) {
        numberOfOps++;
        switch (opcode & 0xF000) {
            case 0x0000: {
                switch (opcode) {
                    case 0x00E0: {
                        // Clear screen
                        clearGFX();
                        pc += 2;
                    } break;
                    case 0x00EE: {
                        // Return from subroutine
                        pc = stack[sp];
                        sp--;
                    } break;
                    default: {
                        throw new IllegalStateException("Unknown Opcode Fetched" +  BinaryLogic.shortToHexString(opcode));
                    }
                }
            } break;
            case 0x1000: {
                // Jump to address
                short data = (short)(opcode & 0x0FFF);
                pc = data;
            } break;
            case 0x2000: {
                // Call routine at address
                short data = (short)(opcode & 0x0FFF);
                sp++;
                stack[sp] = pc;
                pc = data;
            } break;
            case 0x3000: {
                // Skip next instruction if Vx = kk.
                short reg = (short)((opcode & 0x0F00) >> 8);
                byte lhs = (byte)((opcode & 0x00FF));
                if(V[reg] == lhs) {
                    pc += 2;
                }
                pc += 2;
            } break;
            case 0x4000: {
                // Skip next instruction if Vx != kk.
                short reg = (short)((opcode & 0x0F00) >> 8);
                byte lhs = (byte)((opcode & 0x00FF));
                if(V[reg] != lhs) {
                    pc += 2;
                }
                pc += 2;
            } break;
            case 0x5000: {
                // Skip next instruction if Vx = Vy.
                short lhs = (short)((opcode & 0x0F00) >> 8);
                short rhs = (short)((opcode & 0x00F0) >> 4);
                if(V[lhs] == V[rhs]) {
                    pc += 2;
                }
                pc += 2;
            } break;
            case 0x6000: {
                // The interpreter puts the value kk into register Vx.
                short reg = (short)((opcode & 0x0F00) >> 8);
                byte data = (byte)(opcode & 0x00FF);
                V[reg] = data;
                pc += 2;
            } break;
            case 0x7000: {
                // Set Vx = Vx + kk.
                short reg = (short)((opcode & 0x0F00) >> 8);
                byte data = (byte)(opcode & 0x00FF);
                V[reg] += data;
                pc += 2;
            } break;
            case 0x8000: {
                short lhs = (short)((opcode & 0x0F00) >> 8);
                short rhs = (short)((opcode & 0x00F0) >> 4);
                switch (opcode & 0x000F) {
                    case 0x0000: {
                        V[lhs] = V[rhs];
                        pc += 2;
                    } break;
                    case 0x0001: {
                        V[lhs] = (byte)(V[lhs] | V[rhs]);
                        pc += 2;
                    } break;
                    case 0x0002: {
                        V[lhs] = (byte)(V[lhs] & V[rhs]);
                        pc += 2;
                    } break;
                    case 0x0003: {
                        V[lhs] = (byte)(V[lhs] ^ V[rhs]);
                        pc += 2;
                    } break;
                    case 0x0004: {
                        int temp = (V[lhs] + V[rhs]);
                        if(temp > 255) {
                            V[Registers.R_F.getValue()] = 1;
                        } else {
                            V[Registers.R_F.getValue()] = 0;
                        }
                        byte data = (byte)(temp);
                        V[lhs] = data;
                        pc += 2;
                    } break;
                    case 0x0005: {
                        if(V[lhs] > V[rhs]) {
                            V[Registers.R_F.getValue()] = 1;
                        } else {
                            V[Registers.R_F.getValue()] = 0;
                        }
                        V[lhs] = (byte)(V[lhs] - V[rhs]);
                        pc += 2;
                    } break;
                    case 0x0006: {
                        V[Registers.R_F.getValue()] = (byte)(V[lhs]&0x01);
                        V[lhs] = (byte)(V[lhs] >> 1);
                        pc += 2;
                    } break;
                    case 0x0007: {
                        if(V[rhs] > V[lhs]) {
                            V[Registers.R_F.getValue()] = 1;
                        } else {
                            V[Registers.R_F.getValue()] = 0;
                        }
                        V[lhs] = (byte)(V[rhs] - V[lhs]);
                        pc += 2;
                    } break;
                    case 0x000E: {
                        V[Registers.R_F.getValue()] = (byte)((V[lhs]&0x80) >> 8);
                        V[lhs] = (byte)(V[lhs] << 1);
                        pc += 2;
                    } break;
                    default: {throw new IllegalStateException("WTF 3");}
                }
            } break;
            case 0x9000: {
                // Skip next instruction if Vx != Vy.
                short lhs = (short)((opcode & 0x0F00) >> 8);
                short rhs = (short)((opcode & 0x00F0) >> 4);
                if(V[lhs] != V[rhs]) {
                    pc += 2;
                }
                pc += 2;
            } break;
            case 0xA000: {
                // Set I = nnn.
                I = (short)(opcode & 0x0FFF);
                pc += 2;
            } break;
            case 0xB000: {
                // Jump to location nnn + V0.
                short data = (short)(opcode & 0x0FFF);
                pc = (short) (V[0] + data);
            } break;
            case 0xC000: {
                // Set Vx = random byte AND kk.
                short lhs = (short)((opcode & 0x0F00) >> 8);
                byte rand = (byte)(random.nextInt(256));
                byte data = (byte)(opcode & 0x00FF);
                V[lhs] = (byte)(rand & data);
                pc += 2;
            } break;
            case 0xD000: {
                //Display n-byte sprite starting at memory location I at (Vx, Vy), set VF = collision.
                //Draws a sprite at coordinate (VX, VY) that has a width of 8 pixels and a height of N pixels. Each row of 8 pixels is read as
                //bit-coded starting from memory location I; I value doesn’t change after the execution of this instruction. As described above,
                //VF is set to 1 if any screen pixels are flipped from set to unset when the sprite is drawn, and to 0 if that doesn’t happen.
                short lhs = (short)((opcode & 0x0F00) >> 8);
                short rhs = (short)((opcode & 0x00F0) >> 4);
                short height = (short)(opcode & 0x000F);
                short pixel;
                short x = V[lhs];
                short y = V[rhs];
                V[Registers.R_F.value] = 0;
                for (int yLine = 0; yLine < height; yLine++) {
                    pixel = memory[I+yLine];
                    for (int xLine = 0; xLine < 8; xLine++) {
                        if((pixel & (0x80 >> xLine)) != 0) {
                            if(gfx[(x+xLine + ((y + yLine) * 64))] == 1) {
                                V[Registers.R_F.value] = 1;
                            }
                            gfx[(x+xLine + ((y + yLine) * 64))] ^= 1;
                        }
                    }
                }
                drawFlag = true;
                pc += 2;
            } break;
            case 0xE000: {
                short lhs = (short)((opcode & 0x0F00) >> 8);

                switch (opcode & 0x00FF) {
                    case 0x009E: {
                        // Skip next instruction if key with the value of Vx is pressed.
                        if(key[V[lhs]] == 1) {
                            pc += 2;
                        }
                        pc += 2;
                        clearKeys();
                    } break;
                    case 0x00A1: {
                        // Skip next instruction if key with the value of Vx is not pressed.
                        if(key[V[lhs]] == 0) {
                            pc += 2;
                        }
                        pc += 2;
                        clearKeys();
                    } break;
                    default: {
                        throw new IllegalStateException("WTF 4");
                    }
                }
            } break;
            case 0xF000: {
                short lhs = (short)((opcode & 0x0F00) >> 8);
                switch (opcode & 0x00FF) {
                    case 0x0007: {
                        // Fx07 - LD Vx, DT
                        V[lhs] = delayTimer;
                        pc += 2;
                    } break;
                    case 0x000A: {
                        System.out.println("HALTING COMMAND");
                        // Wait for a key press, store the value of the key in Vx.
                        // All execution stops until a key is pressed, then the value of that key is stored in Vx.
                        halt = true;
                        while(halt) { System.err.println("HALTING");}
                        V[lhs] = (byte)lastKey;
                        pc += 2;
                    } break;
                    case 0x0015: {
                        //Set delay timer = Vx.
                        //DT is set equal to the value of Vx.
                        delayTimer = V[lhs];
                        pc += 2;
                    } break;
                    case 0x0018: {
                        //Set sound timer = Vx.
                        //ST is set equal to the value of Vx.
                        soundTimer = V[lhs];
                        pc += 2;
                    } break;
                    case 0x001E: {
                        // Set I = I + Vx.
                        // The values of I and Vx are added, and the results are stored in I.
                        I = (short)(I + V[lhs]);
                        pc += 2;
                    } break;
                    case 0x0029: {
                        // Set I = location of sprite for digit Vx.
                        // The value of I is set to the location for the hexadecimal sprite corresponding to the value of Vx.
                        I = (short)(0x50 + 5* V[lhs]);
                        pc += 2;
                    } break;
                    case 0x0033: {
                        // Store BCD representation of Vx in memory locations I, I+1, and I+2.
                        // The interpreter takes the decimal value of Vx, and places the hundreds digit in memory at location in I, the tens digit at location I+1, and the ones digit at location I+2.
                        memory[I]     = (byte)(V[lhs] / 100);
                        memory[I + 1] = (byte)((V[lhs] / 10) % 10);
                        memory[I + 2] = (byte)((V[lhs] % 100) % 10);
                        pc += 2;
                    } break;
                    case 0x0055: {
                        // Store registers V0 through Vx in memory starting at location I.
                        // The interpreter copies the values of registers V0 through Vx into memory, starting at the address in I.
                        for (int i = 0; i < V[lhs]; i++) {
                            memory[I+i] = V[i];
                        }
                        pc += 2;
                    } break;
                    case 0x0065: {
                        // Read registers V0 through Vx from memory starting at location I.
                        // The interpreter reads values from memory starting at location I into registers V0 through Vx.
                        for (int i = 0; i < V[lhs]; i++) {
                            V[i] = memory[I+i];
                        }
                        pc += 2;
                    } break;
                    default: {
                        throw new IllegalStateException("WTF");
                    }
                }
            } break;
            default: {
                throw new IllegalStateException("WTF UNICORNS");
            }
        }

    }

    //-------------------------------------------------------------------

    @Override
    public void keyTyped(KeyEvent e) {}

    @Override
    public void keyPressed(KeyEvent e) {

    }

    @Override
    public void keyReleased(KeyEvent e) {
        switch (e.getKeyCode()) {
            case KeyEvent.VK_1: { lastKey = 0x1; key[0x1] = 1; } break;
            case KeyEvent.VK_2: { lastKey = 0x2; key[0x2] = 1; } break;
            case KeyEvent.VK_3: { lastKey = 0x3; key[0x3] = 1; } break;
            case KeyEvent.VK_4: { lastKey = 0xC; key[0xC] = 1; } break;
            case KeyEvent.VK_Q: { lastKey = 0x4; key[0x4] = 1; } break;
            case KeyEvent.VK_W: { lastKey = 0x5; key[0x5] = 1; } break;
            case KeyEvent.VK_E: { lastKey = 0x6; key[0x6] = 1; } break;
            case KeyEvent.VK_R: { lastKey = 0xD; key[0xD] = 1; } break;
            case KeyEvent.VK_A: { lastKey = 0x7; key[0x7] = 1; } break;
            case KeyEvent.VK_S: { lastKey = 0x8; key[0x8] = 1; } break;
            case KeyEvent.VK_D: { lastKey = 0x9; key[0x9] = 1; } break;
            case KeyEvent.VK_F: { lastKey = 0xE; key[0xE] = 1; } break;
            case KeyEvent.VK_Z: { lastKey = 0xA; key[0xA] = 1; } break;
            case KeyEvent.VK_X: { lastKey = 0x0; key[0x0] = 1; } break;
            case KeyEvent.VK_C: { lastKey = 0xB; key[0xB] = 1; } break;
            case KeyEvent.VK_V: { lastKey = 0xF; key[0xF] = 1; } break;
        }
        halt = false;
    }

    @Override
    public String toString() {
        return "CPU{" +
                "pc=" + Integer.toHexString(pc) +
                "instruction="+BinaryLogic.shortToHexString(BinaryLogic.twoBytesToShort(memory[pc], memory[pc+1]))+
                "sp=" + sp +
                '}';
    }
}
