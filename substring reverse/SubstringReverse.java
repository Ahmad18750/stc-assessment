import java.util.*;

public class SubstringReverse {

    public static void main(String[] args) {
        Set<String> s = Set.of("abd(jnb)asdf", "abdjnbasdf", "dd(df)a(ghhh)", "ab((cd)f)(g)");
        for(String str: s)
            reverseNested(str, false);
    }

    public static void reverseNested(String s, boolean swapNestedParentheses) {
        int n = s.length();
        Stack<Integer> parenthesesIndexes = new Stack<>();
        Map<Integer, Integer> closedOpenedMap = new HashMap<>();
        for (int i = 0; i < n; ++i) {
            if (s.charAt(i) == '(')
                parenthesesIndexes.push(i);
            if (s.charAt(i) == ')') {
                closedOpenedMap.put(i, parenthesesIndexes.pop());
            }
        }

        char[] arr = s.toCharArray();
        Queue<Character> queue = new ArrayDeque<>();

        for (Integer jj : closedOpenedMap.keySet()) {
            int j = jj - 1;
            for (; j > closedOpenedMap.get(jj); j--) {
                if(!swapNestedParentheses) {
                    if (arr[j] == '(')
                        queue.add(')');
                    else if (arr[j] == ')')
                        queue.add('(');
                    else
                    queue.add(arr[j]);
                }
                else {
                    queue.add(arr[j]);
                }
            }
            while (!queue.isEmpty()) {
                arr[++j] = queue.poll();
            }
        }
        printOutput(s, arr);
    }

    public static void printOutput(String s, char[] arr) {
        System.out.println(s);
        for (char c : arr)
            System.out.print(c);
        System.out.println();
        System.out.println();
    }
}