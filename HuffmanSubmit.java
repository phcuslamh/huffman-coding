// Name: Phuc Huu Lam
// NetID: plam6
// I do not collaborate with anyone else. 

import java.io.*;
import java.util.*;

public class HuffmanSubmit implements Huffman {
	
	public static class MyNode implements Comparable<MyNode> {
		private MyNode leftChild;
		private MyNode rightChild;
		private Character character;
        private int frequency;
       
        //Constructor
		public MyNode(MyNode left, MyNode right, Character c, int n) {
			this.leftChild = left;
			this.rightChild = right;
			this.character = c;
			this.frequency = n;
		}
		
		//Check if node is a leaf
		public boolean isLeaf() {return ((leftChild == null) && (rightChild == null));}
		
		//Compare nodes by their frequency
		public int compareTo(MyNode other) {return (this.frequency - other.frequency);}
	}
	
	//Sort HashMap by value
	private static HashMap<Character, Integer> sortByValue(HashMap<Character, Integer> dict) {
        List<Map.Entry<Character, Integer>> list = new LinkedList<Map.Entry<Character, Integer>>(dict.entrySet());
 
        Collections.sort(list, new Comparator<Map.Entry<Character, Integer>>() {
            public int compare(Map.Entry<Character, Integer> entry1, Map.Entry<Character, Integer> entry2) {
                return (entry1.getValue()).compareTo(entry2.getValue());
            }
        });
        
        HashMap<Character, Integer> temp = new LinkedHashMap<Character, Integer>();
        for (Map.Entry<Character, Integer> entry : list) {temp.put(entry.getKey(), entry.getValue());}
        return temp;
    }
	
	//Store frequency of characters of a string in a HashMap (sorted by value)
	private static HashMap<Character, Integer> freqTable(String str) {
		HashMap<Character, Integer> freqReturn = new HashMap<Character, Integer>();
		char[] conv = str.toCharArray();
		for (int i = 0; i < conv.length; i++) {
			if (freqReturn.containsKey(conv[i])) {freqReturn.put(conv[i], freqReturn.get(conv[i]) + 1);}
			else {freqReturn.put(conv[i], 1);}
		}
		return sortByValue(freqReturn);
	}
	
	//Remove character with minimum frequency from HashMap
	private static Map.Entry<Character, Integer> deleteMin(HashMap<Character, Integer> freqtable) {		
		Integer min = Collections.min(freqtable.values());
		for (Map.Entry<Character, Integer> pair : freqtable.entrySet()) {
			if (pair.getValue().equals(min)) {
				freqtable.remove(pair.getKey());
				return pair;
			}
		}
		return null;
	}
	
	//Store encoded characters into a HashMap
	private static void encodeTable(HashMap<Character, String> table, MyNode x, String str) {
		if (x.isLeaf()) {table.put(x.character, str);}
		else {
			encodeTable(table, x.leftChild, str + "0");
			encodeTable(table, x.rightChild, str + "1");
		}
	}
	
	//Comparator TreeNode class implementation
    private static Comparator<MyNode> comp = new Comparator<MyNode>(){
         
        @Override
        public int compare(MyNode c1, MyNode c2) {
            return (c1.frequency - c2.frequency);
        }
    };
	
	//Build Huffman Tree (given frequencies) 
	private static MyNode buildTrie(HashMap<Character, Integer> freqtable) {
		HashMap<Character, Integer> freqtableCopy = freqtable;
		PriorityQueue<MyNode> priorQueue = new PriorityQueue<MyNode>(freqtableCopy.size(), comp);
		
		//Create leaves
		while (freqtableCopy.size() > 0) {
			Map.Entry<Character, Integer> leaf = deleteMin(freqtableCopy);
			priorQueue.add(new MyNode(null, null, leaf.getKey(), leaf.getValue()));
		}
		
		//Merge leaves
		while (priorQueue.size() > 1) {
			MyNode leftC = priorQueue.remove();
			MyNode rightC = priorQueue.remove();
			priorQueue.add(new MyNode(leftC, rightC, null, leftC.frequency + rightC.frequency));
		}
		return priorQueue.peek();
    }

    //Build Huffman dict (with tree)
	private static HashMap<Character, String> HuffDict(HashMap<Character, Integer> freqtable) {
		MyNode root = buildTrie(freqtable);
		HashMap<Character, String> dict = new HashMap<Character, String>();
		encodeTable(dict, root, "");
		return dict;
	}
	
	//convert character to BinaryString
	private static String valueToBinary(char character) {
       char [] toReturn = {'0','0','0','0','0','0','0','0'};
       int v = (int)character & 0xFFFF;
       for (int i = 0; v > 0; v >>= 1, i++) {
          if ((v & 1) == 1) {toReturn[7 - i] = '1';}
       }
       return new String(toReturn);
    }
	
	//convert 8-bit BinaryString to character
	private static char binaryToValue(String binary) {
	   int parseInt = Integer.parseInt(binary, 2);
	   return (char)parseInt;
	}
	
	//------------------------
	//--------ENCODING--------
	//------------------------
	public void encode(String inputFile, String outputFile, String freqFile)  {
		BinaryIn BinaryStdIn = new BinaryIn(inputFile);
		BinaryOut BinaryStdOut = new BinaryOut(outputFile);
		
		//Read Input
		String str = BinaryStdIn.readString();
		char[] charIn = str.toCharArray();
		
		//Store frequency 
		HashMap<Character, Integer> freqtable = freqTable(str);
		
		//Write into freqFile
		try {
			FileWriter myWriter = new FileWriter(freqFile);
			for (Map.Entry<Character, Integer> entry : freqtable.entrySet()) {
				Character c = entry.getKey();
				Integer s0 = entry.getValue();
				String cString = valueToBinary(c);
				myWriter.write(cString + ":" + s0);
				myWriter.write("\n");
			}
			myWriter.close();
		} 
		catch (IOException e) {System.out.println("IOException");}
		
		//Write into outputFile
		HashMap<Character, String> translated = HuffDict(freqtable);
		for (int i = 0; i < charIn.length; i++) {
			String s1 = translated.get(charIn[i]);
			for (int j = 0; j < s1.length(); j++) {
                if (s1.charAt(j) == '0') {
                    BinaryStdOut.write(false);
                }
                else if (s1.charAt(j) == '1') {
                    BinaryStdOut.write(true);
                }
                else throw new IllegalStateException("Illegal state");
            }
		}
		BinaryStdOut.flush();
		BinaryStdOut.close();
	}

	//------------------------
	//--------DECODING--------
	//------------------------
	public void decode(String inputFile, String outputFile, String freqFile) {
		BinaryIn BinaryStdIn = new BinaryIn(inputFile);
		BinaryOut BinaryStdOut = new BinaryOut(outputFile);
		
		//Rebuild Huffman Tree from freqFile
		HashMap<Character, Integer> freqtable = new HashMap<Character, Integer>();
		try {
			Scanner scnr = new Scanner(new File(freqFile));
			while (scnr.hasNextLine()) {
				String line = scnr.nextLine();
				String[] pair = line.split(":");
				char c = binaryToValue(pair[0]);
				int n = Integer.parseInt(pair[1]);
				freqtable.put(c, n);
			}
			scnr.close();
		}
		catch (FileNotFoundException e) {System.out.println("Frequency file not found.");}
		
		//Write into OutputFile
		MyNode root = buildTrie(freqtable);
		boolean Remain = true;
		while (Remain) {
			MyNode x = root;
			while (!x.isLeaf()) {
				boolean bit = BinaryStdIn.readBoolean();
				if (bit) {x = x.rightChild;}
				else {x = x.leftChild;}
			}
			BinaryStdOut.write(x.character, 8);
			Remain = !BinaryStdIn.isEmpty();
		}
		BinaryStdOut.flush();
        BinaryStdOut.close();
	}
	
	public static void main(String[] args) {
		 
	}
}
