import java.util.NoSuchElementException;
import java.util.Scanner;
import java.io.*;
import java.io.PrintStream;

public class FlammiaM_Project5_Java{
	public static void main(String args[]) throws Exception{
		//makes sure arguments are present
		if(args.length != 3){
			System.out.println("To run please include these arguments: inputData debugFile treeFile");
			return;
		}
		//opens inputfile with error handling
		File inFile = new File(args[0]);
		if(!inFile.isFile() && !inFile.canRead()){
			System.out.println("Failed to open file (Was filename typed correctly?)");
			return;
		}
		
		//creates scanner for inFile
		Scanner scan = new Scanner(inFile);
		
		//sets name of output files
		String debugFile = args[1];
		String treeFile = args[2];
		//initalizes new tree
		twoThreeTree tree = new twoThreeTree();
		tree.initialTree(scan, debugFile);
		while(scan.hasNextInt()){
			int data = scan.nextInt();
			twoThreeTree.treeNode spot = tree.findSpot(tree.root, data);
			if(spot == null){
				PrintStream fileOut = new PrintStream(new FileOutputStream(debugFile,true));
				System.setOut(fileOut);
				System.out.println("Data is in the database, not inserting.");
				fileOut.close();
			}
			else{
				spot.printNode(debugFile);
				twoThreeTree.treeNode newNode = tree.new treeNode (data,-1);
				tree.treeInsert(spot,newNode);
				tree.preOrder(tree.root, debugFile);
			}
		}
		tree.preOrder(tree.root, treeFile);
	}
}

class twoThreeTree{
	class treeNode{
		//variables
		int key1;
		int key2;
		treeNode child1;
		treeNode child2;
		treeNode child3;
		treeNode father;
		//constructor
		treeNode(){
			this.key1 = -1;
			this.key2 = -1;
			this.child1 = null;
			this.child2 = null;
			this.child3	= null;
			this.father = null;
		}
		treeNode(int key1, int key2){
			this.key1 = key1;
			this.key2 = key2;
			this.child1 = null;
			this.child2 = null;
			this.child3	= null;
			this.father = null;
		}
		//methods
		boolean isLeaf(){
			if(this.child1 == null && this.child2 == null && this.child3 == null)
				return true;
			else
				return false;
		}
		void printNode(String outFile) throws FileNotFoundException{
			PrintStream fileOut = new PrintStream(new FileOutputStream(outFile,true));
			System.setOut(fileOut);
			if(this.isLeaf()){
				System.out.print(this.key1+" "+this.key2+" null null null ");
				if(this.father != null)
					System.out.println(this.father.key1);
				else
					System.out.println("null");
			}
			else if(!this.isLeaf() && this.child3 != null){
				System.out.print(this.key1+" "+this.key2+" "+this.child1.key1+" "+this.child2.key1+" "+this.child3.key1+" ");
				if(this.father != null)
					System.out.println(this.father.key1);
				else
					System.out.println("null");
			}
			else if(!this.isLeaf() && this.child3 == null){
				System.out.print(this.key1+" "+this.key2+" "+this.child1.key1+" "+this.child2.key1+" null ");
				if(this.father != null)
					System.out.println(this.father.key1);
				else
					System.out.println("null");
			}
			else{
				System.out.println("Critical Error in printNode: No condition met for print statement, terminating");
				fileOut.close();
				System.exit(0);
			}
			fileOut.close();
		}
	}
	//two three tree variables
	treeNode root;
	//constructor
	twoThreeTree(){
		treeNode dummy = new treeNode();
		this.root = new treeNode(-1, -1);
		dummy.child1 = root;
		root.father = dummy;
	}
	//methods
	void preOrder(treeNode root, String outFile) throws FileNotFoundException{
		if(root == null)
			return;
		root.printNode(outFile);
		preOrder(root.child1, outFile);
		preOrder(root.child2, outFile);
		preOrder(root.child3, outFile);
	}
	void initialTree(Scanner scan, String debugFile) throws NoSuchElementException,FileNotFoundException{
		int data1 = scan.nextInt();
		int data2 = scan.nextInt();
		if(data2 < data1){
			int temp = data2;
			data2 = data1;
			data1 = temp;
		}
		treeNode node1 = new treeNode(data1, -1);
		node1.father = this.root;
		treeNode node2 = new treeNode(data2, -1);
		node2.father = root;
		this.root.child1 = node1;
		this.root.child2 = node2;
		this.root.key1 = data2;
		this.root.printNode(debugFile);
	}
	treeNode findSpot(treeNode spot, int data){
		if(spot.child1.isLeaf())
			return spot;
		else{
			if(data == spot.key1 || data == spot.key2)
				return null;
			else if(data < spot.key1)
				return findSpot(spot.child1, data);
			else if(spot.key2 == -1 || data < spot.key2)
				return findSpot(spot.child2, data);
			else if(spot.key2 != -1 && data >= spot.key2)
				return findSpot(spot.child3, data);
			else{
				System.out.println("Critical error in findspot: reached end of conditions, exiting");
				System.exit(0);
			}
		}
		return null;
	}
	int findMinSubtree(treeNode node){
		if(node == null)
			return -1;
		else if(node.isLeaf())
			return node.key1;
		else
			return findMinSubtree(node.child1);
	}
	void updateFather(treeNode fatherNode){
		if(fatherNode == root)
			return;
		fatherNode.key1 = findMinSubtree(fatherNode.child2);
		fatherNode.key2 = findMinSubtree(fatherNode.child3);
		updateFather(fatherNode.father);
	}
	void treeInsert(treeNode spot, treeNode newNode){
		//single node in spot
		if(spot.child1!=null && spot.child2 == null){
			if(spot.child1.key1 < newNode.key1){
				spot.child2 = newNode;
			}
			else{
				spot.child2 = spot.child1;
				spot.child1 = newNode;
			}
			if(spot == spot.father.child2 || spot == spot.father.child3){
				this.updateFather(spot.father);
			}
		}
		//case1
		else if(spot.child1!=null && spot.child2!=null){
			if(newNode.key1 > spot.child1.key1){
				if(newNode.key1>spot.child2.key1){
					spot.child3 = newNode;
				}
				else
					spot.child3 = spot.child2;
					spot.child2 = newNode;
			}
			else{
				spot.child3 = spot.child2;
				spot.child2 = spot.child1;
				spot.child1 = newNode;
			}
			if(spot == spot.father.child2 || spot == spot.father.child3){
				this.updateFather(spot.father);
			}
		}
		//case2
		if(spot.child1 != null && spot.child2 != null && spot.child3 != null){
			treeNode temp;
			if(newNode.key1 > spot.child1.key1){
				if(newNode.key1 > spot.child2.key1){
					if(newNode.key1 > spot.child3.key1){
						temp = newNode;
					}
					else{
						temp = spot.child3;
						spot.child3 = newNode;
					}
				}
				else
					temp = spot.child3;
					spot.child3 = spot.child2;
					spot.child2 = newNode;
			}
			else{
				temp = spot.child3;
				spot.child3 = spot.child2;
				spot.child2 = spot.child1;
				spot.child1 = newNode;
			}
			treeNode sibling = new treeNode();
			sibling.father = spot.father;
			sibling.child2 = temp;
			sibling.child1 = spot.child3;
			spot.child3 = null;
			spot.key1 = this.findMinSubtree(spot.child2);
			spot.key2 = -1;
			sibling.key1 = this.findMinSubtree(sibling.child2);
			sibling.key2 = -1;
			if(spot == spot.father.child2 || spot == spot.father.child3)
				this.updateFather(spot.father);
			if(sibling == sibling.father.child2 || sibling == sibling.father.child3)
				this.updateFather(sibling.father);
		}
	}
}