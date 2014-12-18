import java.io.*;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.util.*;

public class MainCPU {

	private static int pc; //program counter <=0xfff
	private static short ac; //accumulator <= 0xffff
	private static int ir; //instruction register <=0xffff
	private static int mar; //Memory Address Register <=0xfff
	private static Data mbr = new Data(); //Memory Buffer Register <=0xffff
	private static ArrayList<Data> memory = new ArrayList<Data>(); //memory
	private static final int START = 0;
	private static final Charset STANDARD_CHARSET = StandardCharsets.UTF_8;
	private static final String PROGRAM = "/users/Tarkan/Github Repositories/Tarkan's Repositories/workspaceCE101/project.txt";
	private static final int MASK_12BIT = 0xfff;
	private static final int MASK_16BIT = 0xffff;
	private static boolean running = true;
	
	public static void main(String[] args) {
		System.out.println("Start");
		//initialize memory
		initializeMemory(PROGRAM);
		System.out.printf("Loaded %s\n", PROGRAM);
		//start pc at beginning of program
		pc = START;
		//start loop
		while (running) {
			//read data at pc's address
			readData();
			//do command
			interpretInstruction();
			//check for errors (divide by zero, overflow...)
			//mask pc
			pc &= MASK_12BIT;
		}
		//end if pc is negative
		System.out.println("Done.");
	}
	
	private static void readData() {
		//copy pc to mar
		mar = pc;
		//copy m[mar] to ir
		ir = memory.get(mar).getWord() & MASK_16BIT;
		System.out.printf("0x%03X: 0x%04X | ", pc, ir);
		//increment pc
		pc++;
		//place address into mar
		mar = (new Data((short) ir)).getAddress();
	}
	
	private static void interpretInstruction() {
		//read ir
		//run one of the following methods
		switch ((new Data((short) ir)).getOpcode()) {
			case 0x01: load();
				break;
			case 0x02: store();
				break;
			case 0x03: add();
				break;
			case 0x04: subt();
				break;
			case 0x05: input();
				break;
			case 0x06: output();
				break;
			case 0x07: halt();
				break;
			case 0x08: skipcond();
				break;
			case 0x09: jump();
			default: break;
		}
	}
	
	/* private static void initializeMemory(String fileName) throws IOException {
		Path path = Paths.get(fileName);
		List<String> memoryStrings = new ArrayList<String>();
		memoryStrings = Files.readAllLines(path, STANDARD_CHARSET);
		for (String coded : memoryStrings) {
			int data = 0;
			for (int i = 0; i < coded.length(); i++) {
				if (coded.charAt(i) == '1') {
					data += 2^(coded.length() - i - 1);
				}
			}
			memory.add(new Data((short) data));
		}
	}
	*/
	
	private static void initializeMemory(String fileName) {
		try {
			InputStream fis;
			fis = new FileInputStream(PROGRAM);
			BufferedReader br = new BufferedReader(new InputStreamReader(fis, Charset.forName("UTF-8")));
			String code;
			int counter = 0;
			while ((code = br.readLine()) != null) {
				memory.add(new Data((short) Integer.parseInt(code.substring(2, 6), 16)));
				System.out.printf("0x%03X: 0x%04X\n", counter, memory.get(memory.size()-1).getWord());
				counter++;
			}
			br.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
		
	}
	
	//code 0x1
	private static void load() {
		//mar ==> ac
		mar = (new Data((short) ir)).getAddress();
		mbr = memory.get(mar);
		ac = mbr.getWord();
		System.out.printf("Load: 0x%03X 0x%04X (%d)\n", mar, ac, ac);
	}
	
	//code 0x2
	private static void store() {
		//ac ==> m[mar]
		mar = (new Data((short) ir)).getAddress();
		mbr = new Data(ac);
		memory.set(mar, mbr);
		System.out.printf("Store: 0x%03X 0x%04X (%d)\n", mar, ac, ac);
	}
	
	//code 0x3
	private static void add() {
		//ac + mar ==> ac
		mar = (new Data((short) ir)).getAddress();
		mbr = memory.get(mar);
		ac += mbr.getWord();
		// make ac a short
		System.out.printf("Add: 0x%03X 0x%04X (%d)\n", mar, ac, ac);
	}
	
	//code 0x4
	private static void subt() {
		//ac - mar ==> ac
		mar = (new Data((short) ir)).getAddress();
		mbr = memory.get(mar);
		ac -= mbr.getWord();
		// make ac a short
		System.out.printf("Subtract: 0x%03X 0x%04X (%d)\n", mar, ac, ac);
	}
	
	//code 0x5
	private static void input() {
		//input ==> ac
	}
	
	//code 0x6
	private static void output() {
		//ac ==> output
		System.out.printf("Out: AC 0x%04X (%d)\n", ac, ac);
	}
	
	//code 0x7
	private static void halt() {
		System.out.println("Halt");
		running = false;
	}

	//code 0x8
	private static void skipcond() {
		String skipped = "Did not skip";
		if (((new Data((short) ir).getAddress() >>> 10 == 0) && (ac < 0)) || 
				((new Data((short) ir).getAddress() >>> 10 == 1) && (ac == 0)) || 
				((new Data((short) ir).getAddress() >>> 10 == 2) && (ac > 0))) {
			pc++;
			skipped = "Skipped";
		}
		System.out.printf("Skipcond: %s\n", skipped);
	}
	
	//code 0x9
	private static void jump() {
		pc = (new Data((short) ir)).getAddress();
		System.out.printf("Jump: 0x%03X\n", pc);
	}
}
