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
			//fetches data and finds spot to insert it
			int data = scan.nextInt();
			twoThreeTree.treeNode spot = tree.findSpot(tree.root, data);
			if(spot == null){
				//if data is already in database
				PrintStream fileOut = new PrintStream(new FileOutputStream(debugFile,true));
				System.setOut(fileOut);
				System.out.println("\nData is in the database, not inserting.\n");
				fileOut.close();
			}
			else{
				//inserting new data
				PrintStream fileOut = new PrintStream(new FileOutputStream(debugFile,true));
				System.setOut(fileOut);
				System.out.println("\nData to insert:"+data);
				System.out.println("Printing spot node...");
				spot.printNode(debugFile);
				//creates node to insert and inserts it
				twoThreeTree.treeNode newNode = tree.new treeNode (data,-1);
				tree.treeInsert(spot,newNode);
				//prints the preorder of current tree post-insertion
				System.setOut(fileOut);
				System.out.println("\nPrinting post insertion");
				tree.preOrder(tree.root, debugFile);
			}
		}
		//prints final tree to tree output file
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
		//checks if node is a leaf
		boolean isLeaf(){
			if(this.child1 == null && this.child2 == null && this.child3 == null)
				return true;
			else
				return false;
		}
		//checks if node is full
		boolean isFull(){
			if(this.child1 != null && this.child2 != null && this.child3 != null)
				return true;
			else
				return false;
		}
		//prints nodes information, accounts for null values
		void printNode(String outFile) throws FileNotFoundException{
			//defines output file
			PrintStream fileOut = new PrintStream(new FileOutputStream(outFile,true));
			System.setOut(fileOut);
			//if leaf, it will have no children
			if(this.isLeaf()){
				System.out.print(this.key1+" "+this.key2+" null null null ");
				//checks if father is null, and decides if to print "null" or not
				if(this.father != null)
					System.out.println(this.father.key1);
				else
					System.out.println("null");
			}
			//if a 2 child situation, prints null for child 3
			else if(!this.isLeaf() && this.child3 == null){
				System.out.print(this.key1+" "+this.key2+" "+this.child1.key1+" "+this.child2.key1+" null ");
				//checks for father, same as before
				if(this.father != null)
					System.out.println(this.father.key1);
				else
					System.out.println("null");
			}
			//if a 3 child situation, prints all data
			else if(!this.isLeaf() && this.child3 != null){
				System.out.print(this.key1+" "+this.key2+" "+this.child1.key1+" "+this.child2.key1+" "+this.child3.key1+" ");
				//father check
				if(this.father != null)
					System.out.println(this.father.key1);
				else
					System.out.println("null");
			}
			//if cannot print one of these situations, outputs failure and closes program
			else{
				System.setOut(new PrintStream(new FileOutputStream(FileDescriptor.out)));
				System.out.println("Critical Error in printNode: No condition met for print statement, terminating");
				fileOut.close();
				System.exit(0);
			}
			fileOut.close();
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
	//recursive preorder print
	void preOrder(treeNode node, String outFile) throws FileNotFoundException{
		//break statement
		if(node == null)
			return;
		//comment out if statement to print all nodes
		if(node.isLeaf())
			node.printNode(outFile);
		preOrder(node.child1, outFile);
		preOrder(node.child2, outFile);
		preOrder(node.child3, outFile);
	}
	//inserts first 2 nodes into tree
	void initialTree(Scanner scan, String debugFile) throws NoSuchElementException,FileNotFoundException{
		//gathers data, and sorts it by size
		int data1 = scan.nextInt();
		int data2 = scan.nextInt();
		if(data2 < data1){
			int temp = data2;
			data2 = data1;
			data1 = temp;
		}
		//assigns to tree nodes and places them into root of tree
		treeNode node1 = new treeNode(data1, -1);
		node1.father = this.root;
		treeNode node2 = new treeNode(data2, -1);
		node2.father = root;
		this.root.child1 = node1;
		this.root.child2 = node2;
		this.root.key1 = data2;
		//prints root and preorder traversal
		System.setOut(new PrintStream(new FileOutputStream(debugFile)));
		System.out.println("Printing root and first preorder...");
		this.root.printNode(debugFile);
		this.preOrder(this.root,debugFile);
	}
	//recursive call to find insert location
	treeNode findSpot(treeNode spot, int data){
		//if data exists in database, return null
		if(data == spot.key1 || data == spot.key2)
				return null;
		//if data is a leaf, return key 1 and break
		if(spot.child1.isLeaf())
			return spot;
		else{
			//checks data in comparison to the keys to decide which child to move to
			if(data < spot.key1)
				return findSpot(spot.child1, data);
			else if(spot.key2 == -1 || data < spot.key2)
				return findSpot(spot.child2, data);
			else if(spot.key2 != -1 && data >= spot.key2)
				return findSpot(spot.child3, data);
			//error catch if no child is moved to, outputs and closes program
			else{
				System.setOut(new PrintStream(new FileOutputStream(FileDescriptor.out)));
				System.out.println("Critical error in findspot: reached end of conditions, exiting");
				System.exit(0);
			}
		}
		//blanket case return to satisfy compiler
		return null;
	}
	//recursive call to find smallest value, primarly called to update keys
	int findMinSubtree(treeNode node){
		//break statement if child does not exist
		if(node == null)
			return -1;
		//break statement if location has been reached
		if(node.isLeaf())
			return node.key1;
		//recursive call to go deeper into tree
		else
			return findMinSubtree(node.child1);
	}
	//recursive call to update father keys, moves up through tree
	void updateFather(treeNode fatherNode){
		//if reached the root's father, break
		if(fatherNode == this.dummy)
			return;
		//call to find min of subtree to update keys
		fatherNode.key1 = findMinSubtree(fatherNode.child2);
		fatherNode.key2 = findMinSubtree(fatherNode.child3);
		//recursive call to update father of current node
		updateFather(fatherNode.father);
	}
	//call to create a new node in situation where root is full
	void makeNewRoot(treeNode spot, treeNode sibling){
		//creates a new root
		treeNode newRoot = new treeNode();
		//assigns two subtrees to new roots children
		newRoot.child1 = spot;
		newRoot.child2 = sibling;
		//assigns father pointer for new children
		spot.father = newRoot;
		sibling.father = newRoot;
		//updates this trees root with new root
		this.root = newRoot;
		this.root.father = dummy;
		this.dummy.child1 = this.root;
		//updates roots keys with the min value
		root.key1 = findMinSubtree(root.child2);
		root.key2 = findMinSubtree(root.child3);
	}
	//the main insert function
	void treeInsert(treeNode spot, treeNode newNode){
		//case 1, 2 children in spot
		if(spot.child1!=null && spot.child2!=null && spot.child3 == null){
			//assigns new nodes father as spot
			newNode.father = spot;
			//organizes nodes by key value
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
			//assigns spots children based on key value
			spot.child1 = min;
			spot.child2 = mid;
			spot.child3 = max;
			//updates spots keys with min 
			spot.key1 = findMinSubtree(spot.child2);
			spot.key2 = findMinSubtree(spot.child3);
			//updates father node if the updated spot is child 1 or 2
			if(spot == spot.father.child2 || spot == spot.father.child3){
				this.updateFather(spot.father);
			}
		}
		//case 2, 3 children in spot
		else if(spot.isFull()){
			//updates new nodes father to spot
			newNode.father = spot;
			//organizes all nodes, over being the overload
			treeNode min,mid,max,over;
			min = spot.child1;
			mid = spot.child2;
			max = spot.child3;
			over = newNode;
			//begins sorting downward
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
			//creates new sibling node and assigns its father as spots father
			treeNode sibling = new treeNode();
			sibling.father = spot.father;
			//sets spots children to the smaller values
			spot.child1 = min;
			spot.child2 = mid;
			spot.child3 = null;
			//sets siblings children as the larger values
			sibling.child1 = max;
			sibling.child2 = over;
			sibling.child3 = null;
			//updates spots keys to reflect new children
			spot.key1 = findMinSubtree(spot.child2);
			spot.key2 = -1;
			//updates siblings keys to reflect new children
			sibling.key1 = findMinSubtree(sibling.child2);
			sibling.key2 = -1;
			//updates siblings children's father pointer to sibling
			sibling.child1.father = sibling;
			sibling.child2.father = sibling;
			//if our spot was the root, we must create a new root to store these two subtrees
			if(spot == this.root){
				this.makeNewRoot(spot,sibling);
			}
			//otherwise just insert sibling into the father nodes children
			else
				treeInsert(spot.father, sibling);
			//updates spot and siblings fathers depending on their position as children
			if(spot == spot.father.child2 || spot == spot.father.child3)
				this.updateFather(spot.father);
			/**
			* an observation about this specific code piece below.
			* in the specifications it says to do this code before the insert of the sibling node
			* fundamentally, this means that this if condition would never be run, since sibling wouldn't belong to any child node of its fathers.
			* i hope others realize this, since it does impact the output.
			**/
			if(sibling == sibling.father.child2 || sibling == sibling.father.child3){
				this.updateFather(sibling.father);
			}
		}
		//error catch if insert cannot find insertion method
		else{
			System.setOut(new PrintStream(new FileOutputStream(FileDescriptor.out)));
			System.out.println("Critical Error: Did not insert node");
			System.exit(0);
		}
	}
}