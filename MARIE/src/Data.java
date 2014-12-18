
public class Data {
	
	private short myWord;
	private static final short OPCODE_MASK = 0xf;
	private static final short ADDRESS_MASK = 0xfff;
	private static final short ADDRESS_SIZE = 12;
	
	Data(short word) {
		myWord = word;
	}
	
	Data() {
		myWord = 0;
	}
	
	public short getOpcode() {
		short opcode = myWord;
		opcode >>>= ADDRESS_SIZE; //might need fixing
		opcode &= OPCODE_MASK;
		return opcode;
	}
	
	public short getAddress() {
		short adress = myWord;
		adress &= ADDRESS_MASK;
		return adress;
	}
	
	public short getWord() {
		return myWord;
	}
	
}
