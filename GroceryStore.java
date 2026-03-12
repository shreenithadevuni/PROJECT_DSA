import java.util.*;

/**
 * +=============================================================+
 *   MULTI STORE GROCERY PRICE COMPARISON SYSTEM
 *   DSA Project | Java Console Application
 *   Data Structures: HashMap, LinkedList, ArrayList,
 *                    Priority Queue, Stack, TreeMap
 * +=============================================================+
 */

// -------------------------------------------------------------
// DSA: Custom Linked List Node for Cart
// -------------------------------------------------------------
class CartNode {
    String productName;
    String category;
    String storeName;
    double pricePerKg;
    double quantityKg;
    double totalPrice;
    CartNode next;

    CartNode(String productName, String category, String storeName,
             double pricePerKg, double quantityKg) {
        this.productName = productName;
        this.category    = category;
        this.storeName   = storeName;
        this.pricePerKg  = pricePerKg;
        this.quantityKg  = quantityKg;
        this.totalPrice  = pricePerKg * quantityKg;
        this.next        = null;
    }
}

// -------------------------------------------------------------
// DSA: Singly Linked List for Shopping Cart
// -------------------------------------------------------------
class ShoppingCart {
    private CartNode head;
    private int size;

    ShoppingCart() { head = null; size = 0; }

    void addItem(CartNode node) {
        if (head == null) { head = node; }
        else {
            CartNode cur = head;
            while (cur.next != null) cur = cur.next;
            cur.next = node;
        }
        size++;
    }

    int size()           { return size; }
    CartNode getHead()   { return head; }
    boolean isEmpty()    { return size == 0; }

    double getTotalPrice() {
        double total = 0;
        CartNode cur = head;
        while (cur != null) { total += cur.totalPrice; cur = cur.next; }
        return total;
    }
}

// -------------------------------------------------------------
// DSA: Min-Heap (Priority Queue) for Best Price
// -------------------------------------------------------------
class StorePrice implements Comparable<StorePrice> {
    String storeName;
    double price;

    StorePrice(String storeName, double price) {
        this.storeName = storeName;
        this.price     = price;
    }

    @Override
    public int compareTo(StorePrice other) {
        return Double.compare(this.price, other.price);
    }
}

// -------------------------------------------------------------
// DSA: Stack for Order History
// -------------------------------------------------------------
class OrderStack {
    private Stack<String> stack = new Stack<>();

    void push(String order) { stack.push(order); }
    String pop()            { return stack.isEmpty() ? null : stack.pop(); }
    String peek()           { return stack.isEmpty() ? null : stack.peek(); }
    boolean isEmpty()       { return stack.isEmpty(); }
}

// =============================================================
// MAIN APPLICATION CLASS
// =============================================================
public class GroceryStore {

    static Scanner sc = new Scanner(System.in);

    // -- 5 Grocery Shops --
    static final String[] STORES = {
        "City Fresh Mart",
        "Lakshmi Supermart",
        "Pam Kirana Store",
        "Anand Grocery",
        "Balaji General Store"
    };

    // -- 8 Categories --
    static final String[] CATEGORIES = {
        "Vegetables", "Fruits", "Grains", "Dairy",
        "Oils", "Spices", "Snacks", "Beverages"
    };

    // DSA: HashMap<Category, List<Product>>
    static HashMap<String, List<String>> categoryProducts = new HashMap<>();

    // DSA: TreeMap (sorted) -> HashMap of stores -> price
    static TreeMap<String, HashMap<String, Double>> priceDB = new TreeMap<>();

    // DSA: Linked List for Shopping Cart
    static ShoppingCart cart = new ShoppingCart();

    // DSA: Stack for Order History
    static OrderStack orderHistory = new OrderStack();

    // DSA: HashMap for User Accounts  username -> password
    static HashMap<String, String> userDB = new HashMap<>();
    static String loggedInUser = "";

    static Random rand = new Random(42);

    // ==========================================================
    // INITIALIZE DATA
    // ==========================================================
    static void initData() {
        categoryProducts.put("Vegetables", Arrays.asList(
            "Onion","Tomato","Potato","Carrot","Spinach",
            "Garlic","Ginger","Green Chilli","Brinjal","Capsicum"
        ));
        categoryProducts.put("Fruits", Arrays.asList(
            "Banana","Apple","Mango","Papaya","Grapes",
            "Watermelon","Orange","Pomegranate","Guava","Pineapple"
        ));
        categoryProducts.put("Grains", Arrays.asList(
            "Sona Masoori Rice","Basmati Rice","Wheat Flour",
            "Toor Dal","Moong Dal","Chana Dal","Urad Dal","Semolina (Rava)"
        ));
        categoryProducts.put("Dairy", Arrays.asList(
            "Full Cream Milk","Curd","Paneer","Butter","Cheese Slices",
            "Skimmed Milk","Butter Milk","Condensed Milk","Ghee"
        ));
        categoryProducts.put("Oils", Arrays.asList(
            "Sunflower Oil","Mustard Oil","Coconut Oil",
            "Pure Ghee","Ground Nut Oil","Olive Oil"
        ));
        categoryProducts.put("Spices", Arrays.asList(
            "Red Chili Powder","Turmeric","Coriander Powder",
            "Garam Masala","Cumin Seeds"
        ));
        categoryProducts.put("Snacks", Arrays.asList(
            "Poha","Biscuits","Kurkure","Lays Chips",
            "Haldiram Namkeen","Marie Gold"
        ));
        categoryProducts.put("Beverages", Arrays.asList(
            "Tea","Bru Coffee","Horlicks","Mango Frooti",
            "Lime Juice","Coconut Water","Nescafe"
        ));

        double[][] basePrices = {
            {25,30,20,60,40,200,120,60,35,80},   // Vegetables (per kg)
            {40,150,80,40,100,20,60,150,50,60},   // Fruits (per kg)
            {55,80,35,120,110,75,90,45},           // Grains (per kg)
            {55,50,280,450,30,48,25,90,480},       // Dairy
            {110,120,150,500,120,600},              // Oils (per litre)
            {25,15,20,35,20},                       // Spices (per 100g)
            {15,25,20,20,60,30},                    // Snacks (per pack)
            {300,200,320,20,15,30,550}              // Beverages
        };

        int catIdx = 0;
        for (String cat : CATEGORIES) {
            List<String> prods = categoryProducts.get(cat);
            for (int pi = 0; pi < prods.size(); pi++) {
                String prod = prods.get(pi);
                HashMap<String, Double> storePrices = new HashMap<>();
                double base = basePrices[catIdx][pi];
                for (String store : STORES) {
                    double variation = 0.85 + (rand.nextDouble() * 0.30);
                    double price = Math.round(base * variation * 100.0) / 100.0;
                    storePrices.put(store, price);
                }
                priceDB.put(prod, storePrices);
            }
            catIdx++;
        }
    }

    // ==========================================================
    // UI HELPERS  (all ASCII - no emojis)
    // ==========================================================
    static void clearScreen() {
        System.out.print("\033[H\033[2J");
        System.out.flush();
    }

    static void printBanner() {
        System.out.println();
        System.out.println("  +==============================================================+");
        System.out.println("  |      MULTI STORE GROCERY PRICE COMPARISON SYSTEM            |");
        System.out.println("  |        Compare Prices | Save Money | Shop Smart             |");
        System.out.println("  +==============================================================+");
        System.out.println();
    }

    static void printSeparator() {
        System.out.println("  --------------------------------------------------------------");
    }

    static void printDoubleSeparator() {
        System.out.println("  ==============================================================");
    }

    static void printSectionHeader(String title) {
        System.out.println();
        printDoubleSeparator();
        System.out.println("    " + title);
        printDoubleSeparator();
    }

    // pressEnter -- uses System.in directly to avoid Scanner buffering bug
    static void pressEnter() {
        System.out.print("\n  [ Press ENTER to continue ] ");
        System.out.flush();
        try {
            while (System.in.read() != '\n') {}
        } catch (Exception e) {}
    }

    // ==========================================================
    // REGISTER PAGE -- goes directly to home after registering
    // ==========================================================
    static void showAuthMenu() {
        clearScreen();
        printBanner();
        printSectionHeader("REGISTER -- CREATE YOUR ACCOUNT");
        System.out.println();

        System.out.print("  Full Name        : ");
        String fullName = sc.nextLine().trim();
        while (fullName.isEmpty()) {
            System.out.print("  [!] Name cannot be empty.\n  Full Name        : ");
            fullName = sc.nextLine().trim();
        }

        System.out.print("  Choose Username  : ");
        String username = sc.nextLine().trim();
        while (username.isEmpty() || username.contains(" ")) {
            System.out.print("  [!] No spaces allowed.\n  Choose Username  : ");
            username = sc.nextLine().trim();
        }

        System.out.print("  Choose Password  : ");
        String password = sc.nextLine().trim();
        while (password.length() < 4) {
            System.out.print("  [!] Minimum 4 characters required.\n  Choose Password  : ");
            password = sc.nextLine().trim();
        }

        System.out.print("  Confirm Password : ");
        String confirm = sc.nextLine().trim();
        if (!confirm.equals(password)) {
            System.out.println("  [!] Passwords do not match! Please try again.");
            pressEnter();
            showAuthMenu();
            return;
        }

        // DSA: Store in HashMap O(1)
        userDB.put(username, password);
        loggedInUser = username;

        System.out.println();
        printSeparator();
        System.out.println("  [SUCCESS] Account Created Successfully!");
        System.out.println("  Welcome, " + fullName + "!");
        System.out.println("  Username : " + username);
        printSeparator();
        System.out.println();
        System.out.println("  [1]  View Cart");
        System.out.println("  [2]  Continue to Shopping");
        printSeparator();
        System.out.print("\n  Enter your choice: ");

        int choice = readInt();
        if (choice == 1) {
            showHomePage();
            showCart();
        } else {
            showHomePage();
        }
    }

    // ==========================================================
    // HOME PAGE
    // ==========================================================
    static void showHomePage() {
        while (true) {
            clearScreen();
            printBanner();
            System.out.println("  Logged in as  : " + loggedInUser);
            System.out.println("  Nearby Stores : City Fresh Mart | Lakshmi Supermart | Pam Kirana Store");
            System.out.println("                  Anand Grocery   | Balaji General Store");
            printSeparator();
            System.out.println();
            System.out.println("  PRODUCT CATEGORIES");
            System.out.println();

            String[] tags = {"[VEG]","[FRT]","[GRN]","[DRY]","[OIL]","[SPC]","[SNK]","[BVG]"};
            for (int i = 0; i < CATEGORIES.length; i++) {
                String cat         = CATEGORIES[i];
                List<String> prods = categoryProducts.get(cat);
                System.out.printf("  %s  [%d] %-18s  (%d items)%n",
                    tags[i], (i + 1), cat, prods.size());
                StringBuilder preview = new StringBuilder("           --> ");
                for (int j = 0; j < Math.min(3, prods.size()); j++) {
                    preview.append(prods.get(j));
                    if (j < 2 && j < prods.size() - 1) preview.append(", ");
                }
                if (prods.size() > 3) preview.append("...");
                System.out.println(preview);
                System.out.println();
            }

            printSeparator();
            System.out.println("  [9]  View Cart  (" + cart.size() + " items)");
            System.out.println("  [0]  Exit");
            printSeparator();
            System.out.print("\n  Enter category number (0-9): ");

            int choice = readInt();
            if (choice == 0) {
                System.out.println("\n  Thank you for using Grocery Comparison System! Goodbye!");
                System.exit(0);
            } else if (choice >= 1 && choice <= 8) {
                showCategoryProducts(CATEGORIES[choice - 1]);
            } else if (choice == 9) {
                showCart();
            } else {
                System.out.println("  [!] Invalid choice. Please enter a number between 0 and 9.");
                pressEnter();
            }
        }
    }

    // ==========================================================
    // CATEGORY PRODUCTS PAGE
    // ==========================================================
    static void showCategoryProducts(String category) {
        while (true) {
            clearScreen();
            printBanner();
            printSectionHeader(category.toUpperCase() + " -- SELECT A PRODUCT");
            System.out.println();

            List<String> prods = categoryProducts.get(category);
            for (int i = 0; i < prods.size(); i++) {
                System.out.printf("  [%2d]  %s%n", (i + 1), prods.get(i));
            }

            printSeparator();
            System.out.println("  [ 0]  Back to Home");
            printSeparator();
            System.out.print("\n  Enter product number: ");

            int choice = readInt();
            if (choice == 0) return;
            if (choice >= 1 && choice <= prods.size()) {
                showPriceComparison(prods.get(choice - 1), category);
            } else {
                System.out.println("  [!] Invalid choice. Please try again.");
                pressEnter();
            }
        }
    }

    // ==========================================================
    // PRICE COMPARISON  [DSA: Priority Queue / Min-Heap]
    // ==========================================================
    static void showPriceComparison(String product, String category) {
        clearScreen();
        printBanner();
        printSectionHeader("PRICE COMPARISON -- " + product.toUpperCase());

        HashMap<String, Double> storePrices = priceDB.get(product);

        // DSA: Min-Heap to sort stores by price ascending
        PriorityQueue<StorePrice> minHeap = new PriorityQueue<>();
        for (Map.Entry<String, Double> e : storePrices.entrySet()) {
            minHeap.offer(new StorePrice(e.getKey(), e.getValue()));
        }

        List<StorePrice> sortedStores = new ArrayList<>();
        while (!minHeap.isEmpty()) sortedStores.add(minHeap.poll());

        double minPrice = sortedStores.get(0).price;
        double maxPrice = sortedStores.get(sortedStores.size() - 1).price;

        System.out.println();
        System.out.println("  Product  : " + product);
        System.out.println("  Category : " + category);
        System.out.println();
        System.out.println("  +----+---------------------------+------------+--------------+");
        System.out.println("  | No | Store Name                | Price/Unit | Status       |");
        System.out.println("  +----+---------------------------+------------+--------------+");

        for (int i = 0; i < sortedStores.size(); i++) {
            StorePrice sp = sortedStores.get(i);
            String status;
            if      (sp.price == minPrice) status = "[BEST PRICE] ";
            else if (sp.price == maxPrice) status = "[COSTLY]     ";
            else                           status = "             ";
            System.out.printf("  | %2d | %-25s | Rs.%6.2f  | %s|%n",
                (i + 1), sp.storeName, sp.price, status);
        }
        System.out.println("  +----+---------------------------+------------+--------------+");

        System.out.printf("%n  Best Price   : Rs.%.2f   at   %s%n", minPrice, sortedStores.get(0).storeName);
        System.out.printf("  Max Savings  : Rs.%.2f   (choosing best over costliest store)%n",
                          (maxPrice - minPrice));

        System.out.println();
        printSeparator();
        System.out.println("  Enter store number to add to cart, or [0] to go back:");
        System.out.print("  Enter your choice: ");
        int choice = readInt();

        if (choice >= 1 && choice <= sortedStores.size()) {
            StorePrice selected = sortedStores.get(choice - 1);
            selectQuantityAndAdd(product, category, selected.storeName, selected.price);
        }
    }

    // ==========================================================
    // QUANTITY SELECTION
    // ==========================================================
    static void selectQuantityAndAdd(String product, String category,
                                      String store, double price) {
        clearScreen();
        printBanner();
        printSectionHeader("SELECT QUANTITY -- " + product.toUpperCase());

        System.out.println();
        System.out.println("  Product  : " + product);
        System.out.println("  Store    : " + store);
        System.out.printf ("  Price    : Rs.%.2f per unit/kg%n", price);
        System.out.println();
        System.out.println("  Enter quantity (0 to 10 kg max):");
        System.out.println();
        System.out.println("  [1]  Enter in Grams");
        System.out.println("  [2]  Enter in Kilograms");
        System.out.print("\n  Enter your choice: ");

        int choice = readInt();
        double quantityKg = 0;

        if (choice == 1) {
            System.out.print("  Enter grams (1 to 10000): ");
            double grams = readDouble();
            if (grams <= 0 || grams > 10000) {
                System.out.println("  [!] Invalid. Must be 1g to 10000g.");
                pressEnter(); return;
            }
            quantityKg = grams / 1000.0;
        } else if (choice == 2) {
            System.out.print("  Enter kilograms (0.1 to 10.0): ");
            quantityKg = readDouble();
            if (quantityKg <= 0 || quantityKg > 10) {
                System.out.println("  [!] Invalid. Must be 0.1 to 10.0 kg.");
                pressEnter(); return;
            }
        } else {
            System.out.println("  [!] Invalid choice.");
            pressEnter(); return;
        }

        double totalPrice = price * quantityKg;
        System.out.println();
        printSeparator();
        System.out.printf("  Added : %-22s | %.3f kg | Rs.%.2f%n", product, quantityKg, totalPrice);
        printSeparator();

        // DSA: Add node to Linked List
        CartNode item = new CartNode(product, category, store, price, quantityKg);
        cart.addItem(item);

        System.out.println("  [SUCCESS] Item added to cart!");
        System.out.println();
        System.out.println("  [1]  Continue Shopping");
        System.out.println("  [2]  View Cart and Checkout");
        System.out.print("\n  Enter your choice: ");
        int next = readInt();
        if (next == 2) showCart();
    }

    // ==========================================================
    // CART PAGE  [DSA: Traverse Linked List]
    // ==========================================================
    static void showCart() {
        clearScreen();
        printBanner();
        printSectionHeader("YOUR SHOPPING CART");

        if (cart.isEmpty()) {
            System.out.println("\n  Your cart is empty. Please add some products first.");
            pressEnter(); return;
        }

        System.out.println();
        System.out.println("  +----+----------------------+-------------------------+----------+------------+");
        System.out.println("  | No | Product              | Store                   |  Qty(kg) |  Total(Rs) |");
        System.out.println("  +----+----------------------+-------------------------+----------+------------+");

        CartNode cur      = cart.getHead();
        int idx           = 1;
        double grandTotal = 0;
        double maxTotal   = 0;

        while (cur != null) {
            HashMap<String, Double> storePrices = priceDB.get(cur.productName);
            double maxPrice = Collections.max(storePrices.values());
            maxTotal   += maxPrice * cur.quantityKg;
            grandTotal += cur.totalPrice;
            System.out.printf("  | %2d | %-20s | %-23s | %8.3f | %10.2f |%n",
                idx++, cur.productName, cur.storeName, cur.quantityKg, cur.totalPrice);
            cur = cur.next;
        }

        System.out.println("  +----+----------------------+-------------------------+----------+------------+");
        System.out.println();
        System.out.printf("  Total Items   : %d%n",      cart.size());
        System.out.printf("  Grand Total   : Rs.%.2f%n", grandTotal);
        System.out.printf("  You Save      : Rs.%.2f   (vs buying from costliest store)%n",
                          (maxTotal - grandTotal));

        System.out.println();
        printSeparator();
        System.out.println("  [1]  Proceed to Checkout");
        System.out.println("  [2]  Continue Shopping");
        System.out.println("  [0]  Back to Home");
        printSeparator();
        System.out.print("\n  Enter your choice: ");

        int choice = readInt();
        if      (choice == 1) deliveryAddressPage();
        else if (choice == 0) return;
    }

    // ==========================================================
    // DELIVERY ADDRESS PAGE
    // ==========================================================
    static void deliveryAddressPage() {
        clearScreen();
        printBanner();
        printSectionHeader("DELIVERY ADDRESS");

        System.out.println();
        System.out.println("  Please fill in your delivery details:");
        System.out.println();

        System.out.print("  Full Name        : ");
        String name = sc.nextLine().trim();
        while (name.isEmpty()) {
            System.out.print("  [!] Name cannot be empty.\n  Full Name        : ");
            name = sc.nextLine().trim();
        }

        System.out.print("  Phone Number     : ");
        String phone = sc.nextLine().trim();
        while (!phone.matches("\\d{10}")) {
            System.out.print("  [!] Enter a valid 10-digit number.\n  Phone Number     : ");
            phone = sc.nextLine().trim();
        }

        System.out.print("  House No / Flat  : ");
        String house = sc.nextLine().trim();
        while (house.isEmpty()) {
            System.out.print("  [!] Cannot be empty.\n  House No / Flat  : ");
            house = sc.nextLine().trim();
        }

        System.out.print("  Area / Locality  : ");
        String area = sc.nextLine().trim();
        while (area.isEmpty()) {
            System.out.print("  [!] Cannot be empty.\n  Area / Locality  : ");
            area = sc.nextLine().trim();
        }

        System.out.println();
        printSeparator();
        System.out.println("  DELIVERY ADDRESS SUMMARY");
        System.out.println("  Name    : " + name);
        System.out.println("  Phone   : " + phone);
        System.out.println("  Address : " + house + ", " + area);
        printSeparator();
        System.out.println();
        System.out.println("  [1]  Confirm and Proceed to Payment");
        System.out.println("  [2]  Re-enter Address");
        System.out.print("\n  Enter your choice: ");

        int choice = readInt();
        if      (choice == 1) paymentPage(name, phone, house, area);
        else if (choice == 2) deliveryAddressPage();
    }

    // ==========================================================
    // PAYMENT PAGE (UPI)
    // ==========================================================
    static void paymentPage(String name, String phone, String house, String area) {
        clearScreen();
        printBanner();
        printSectionHeader("PAYMENT -- UPI");

        double total = cart.getTotalPrice();
        System.out.println();
        System.out.printf("  Amount to Pay  : Rs.%.2f%n", total);
        System.out.println();
        System.out.println("  Payment Method : UPI");
        System.out.println("  Accepted Apps  : GPay | PhonePe | Paytm | BHIM");
        System.out.println();
        System.out.print("  Enter your UPI ID (e.g., yourname@upi): ");
        String upi = sc.nextLine().trim();

        while (!upi.contains("@") || upi.length() < 5) {
            System.out.print("  [!] Invalid UPI ID. Example: name@okaxis\n  Enter UPI ID: ");
            upi = sc.nextLine().trim();
        }

        System.out.println();
        System.out.print("  Processing payment");
        simulateLoading(2);

        System.out.println();
        System.out.println("  +------------------------------------------+");
        System.out.println("  |           PAYMENT SUCCESSFUL!            |");
        System.out.printf ("  |  Amount Paid  : Rs. %-20.2f|%n", total);
        System.out.printf ("  |  UPI ID       : %-22s|%n", upi);
        System.out.println("  +------------------------------------------+");
        pressEnter();

        placeOrderPage(name, phone, house, area, upi, total);
    }

    // ==========================================================
    // ORDER CONFIRMATION PAGE
    // ==========================================================
    static void placeOrderPage(String name, String phone,
                                String house, String area,
                                String upi, double total) {
        clearScreen();
        printBanner();

        String orderId  = "GCS" + (100000 + rand.nextInt(899999));
        int deliveryMin = 30 + rand.nextInt(31);

        // DSA: Push to Order Stack
        orderHistory.push("Order#" + orderId + " | Rs." + String.format("%.2f", total)
                + " | " + name + " | " + area);

        printDoubleSeparator();
        System.out.println("          *** ORDER PLACED SUCCESSFULLY! ***");
        printDoubleSeparator();
        System.out.println();
        System.out.println("  +--------------------------------------------------+");
        System.out.println("  |              ORDER CONFIRMATION                  |");
        System.out.printf ("  |  Order ID    : %-33s|%n", orderId);
        System.out.printf ("  |  Name        : %-33s|%n", name);
        System.out.printf ("  |  Phone       : %-33s|%n", phone);
        System.out.printf ("  |  Address     : %-33s|%n", house + ", " + area);
        System.out.printf ("  |  Amount Paid : Rs. %-29.2f|%n", total);
        System.out.printf ("  |  UPI ID      : %-33s|%n", upi);
        System.out.printf ("  |  Items       : %-33d|%n", cart.size());
        System.out.println("  +--------------------------------------------------+");
        System.out.println("  |                                                  |");
        System.out.printf ("  |  Estimated Delivery : %d - %d minutes           |%n",
                           deliveryMin, deliveryMin + 10);
        System.out.println("  |  Status : Your order is packed & dispatched!    |");
        System.out.println("  |                                                  |");
        System.out.println("  +--------------------------------------------------+");
        System.out.println();
        System.out.println("  ORDER ITEMS SUMMARY:");
        printSeparator();

        CartNode cur = cart.getHead();
        int idx = 1;
        while (cur != null) {
            System.out.printf("  %d. %-22s | %-20s | %.3f kg | Rs.%.2f%n",
                idx++, cur.productName, cur.storeName, cur.quantityKg, cur.totalPrice);
            cur = cur.next;
        }
        printSeparator();
        System.out.printf("  TOTAL PAID : Rs.%.2f%n", total);
        System.out.println();
        System.out.println("  Thank you, " + name + "! Happy Shopping!");
        System.out.println();
        printDoubleSeparator();
        System.out.println("  YOUR ORDER WILL BE DELIVERED IN : " + deliveryMin + " - " + (deliveryMin + 10) + " MINUTES");
        printDoubleSeparator();
        System.out.println();
        printSeparator();
        System.out.println("  [1]  Back to Home (New Order)");
        System.out.println("  [0]  Exit");
        printSeparator();
        System.out.print("\n  Enter your choice: ");

        int choice = readInt();
        if (choice == 1) {
            cart = new ShoppingCart(); // reset cart for new order
            showHomePage();
        } else {
            System.out.println("\n  Thank you! Goodbye!");
            System.exit(0);
        }
    }

    // ==========================================================
    // HELPER: Loading dots  (no special chars)
    // ==========================================================
    static void simulateLoading(int seconds) {
        for (int i = 0; i < seconds * 5; i++) {
            System.out.print(".");
            System.out.flush();
            try { Thread.sleep(200); } catch (InterruptedException e) {}
        }
        System.out.println("  Done!");
    }

    // ==========================================================
    // HELPER: Safe integer input
    // ==========================================================
    static int readInt() {
        try {
            String line = sc.nextLine().trim();
            return Integer.parseInt(line);
        } catch (NumberFormatException e) {
            return -1;
        }
    }

    // ==========================================================
    // HELPER: Safe double input
    // ==========================================================
    static double readDouble() {
        try {
            String line = sc.nextLine().trim();
            return Double.parseDouble(line);
        } catch (NumberFormatException e) {
            return -1;
        }
    }

    // ==========================================================
    // MAIN ENTRY POINT
    // ==========================================================
    public static void main(String[] args) {
        initData();
        showAuthMenu();
    }
}
