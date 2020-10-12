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
				System.out.println("\nData is in the database, not inserting.\n");
				fileOut.close();
			}
			else{
				PrintStream fileOut = new PrintStream(new FileOutputStream(debugFile,true));
				System.setOut(fileOut);
				System.out.println("\nData to insert:"+data);
				System.out.println("Printing spot node...");
				spot.printNode(debugFile);
				
				twoThreeTree.treeNode newNode = tree.new treeNode (data,-1);
				tree.treeInsert(spot,newNode);
				
				System.setOut(fileOut);
				System.out.println("\nPrinting post insertion");
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
		boolean isFull(){
			if(this.child1 != null && this.child2 != null && this.child3 != null)
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
			else if(!this.isLeaf() && this.child2 == null && this.child3 == null){
				System.out.print(this.key1+" "+this.key2+" "+this.child1.key1+" null null ");
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
				System.setOut(new PrintStream(new FileOutputStream(FileDescriptor.out)));
				System.out.println("Critical Error in printNode: No condition met for print statement, terminating");
				fileOut.close();
				System.exit(0);
			}
			fileOut.close();
			System.setOut(new PrintStream(new FileOutputStream(FileDescriptor.out)));
		}
	}
	//two three tree variables
	treeNode root;
	treeNode dummy;
	//constructor
	twoThreeTree(){
		this.root = new treeNode(-1, -1);
		this.dummy = new treeNode(-9, -9);
		this.dummy.child1 = this.root;
		this.root.father = this.dummy;
	}
	//methods
	void preOrder(treeNode node, String outFile) throws FileNotFoundException{
		if(node == null)
			return;
		//if(node.isLeaf())
			node.printNode(outFile);
		preOrder(node.child1, outFile);
		preOrder(node.child2, outFile);
		preOrder(node.child3, outFile);
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
		
		System.setOut(new PrintStream(new FileOutputStream(debugFile)));
		System.out.println("\nPrinting root and first preorder...");
		this.root.printNode(debugFile);
		this.preOrder(this.root,debugFile);
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
				System.setOut(new PrintStream(new FileOutputStream(FileDescriptor.out)));
				System.out.println("Critical error in findspot: reached end of conditions, exiting");
				System.exit(0);
			}
		}
		return null;
	}
	
	int findMinSubtree(treeNode node){
		if(node == null)
			return -1;
		if(node.isLeaf())
			return node.key1;
		else
			return findMinSubtree(node.child1);
	}
	
	void updateFather(treeNode fatherNode){
		if(fatherNode == null || fatherNode == this.dummy)
			return;
		fatherNode.key1 = findMinSubtree(fatherNode.child2);
		fatherNode.key2 = findMinSubtree(fatherNode.child3);
		updateFather(fatherNode.father);
	}
	
	void makeNewRoot(treeNode spot, treeNode sibling){
		treeNode newRoot = new treeNode();
		newRoot.child1 = spot;
		newRoot.child2 = sibling;
		spot.father = newRoot;
		sibling.father = newRoot;
		this.root = newRoot;
		this.root.father = dummy;
		this.dummy.child1 = this.root;
		root.key1 = findMinSubtree(root.child2);
		root.key2 = findMinSubtree(root.child3);
	}
	
	void treeInsert(treeNode spot, treeNode newNode){
		//case1 2 children
		if(spot.child1!=null && spot.child2!=null && spot.child3 == null){
			newNode.father = spot;
			treeNode min,mid,max;
			min = spot.child1;
			mid = spot.child2;
			max = newNode;
			if(min.key1 > max.key1){
				treeNode temp = max;
				max = mid;
				mid = min;
				min = temp;
			}
			else if(mid.key1 > max.key1){
				treeNode temp = max;
				max = mid;
				mid = temp;
			}
			
			spot.child1 = min;
			spot.child2 = mid;
			spot.child3 = max;
			
			spot.key1 = findMinSubtree(spot.child2);
			spot.key2 = findMinSubtree(spot.child3);
			
			if(spot == spot.father.child2 || spot == spot.father.child3){
				this.updateFather(spot.father);
			}
		}
		
		//case2 3 children
		else if(spot.isFull()){
			newNode.father = spot;
			treeNode min,mid,max,over;
			
			min = spot.child1;
			mid = spot.child2;
			max = spot.child3;
			over = newNode;
			
			if(min.key1 > over.key1){
				treeNode temp = over;
				over = max;
				max = mid;
				mid = min;
				min = temp;
			}
			else if(mid.key1 > over.key1){
				treeNode temp = over;
				over = max;
				max = mid;
				mid = temp;
			}
			else if(max.key1 > over.key1){
				treeNode temp = over;
				over = max;
				max = temp;
			}
			
			treeNode sibling = new treeNode();
			sibling.father = spot.father;
			
			spot.child1 = min;
			spot.child2 = mid;
			spot.child3 = null;
			
			sibling.child1 = max;
			sibling.child2 = over;
			sibling.child3 = null;
			
			spot.key1 = findMinSubtree(spot.child2);
			spot.key2 = -1;
			
			sibling.key1 = findMinSubtree(sibling.child2);
			sibling.key2 = -1;
			
			if(spot == spot.father.child2 || spot == spot.father.child3)
				this.updateFather(spot.father);
			if(sibling == sibling.father.child2 || sibling == sibling.father.child3)
				this.updateFather(sibling.father);
			
			//if root
			if(spot == this.root){
				this.makeNewRoot(spot,sibling);
			}
			else
				treeInsert(spot.father, sibling);
		}
		else{
			System.setOut(new PrintStream(new FileOutputStream(FileDescriptor.out)));
			System.out.println("Critical Error: Did not insert node");
			System.exit(0);
		}
	}
}