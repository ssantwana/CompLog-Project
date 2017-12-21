package aspsolver;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.Iterator;
import java.util.LinkedList;

public class ASPSolver {

    public static void main(String[] args) {
        Prog program = new Prog()          
                
                .add( new Rule() .setHead(new Literal("fly(mouse)")) .setPos(new Literal("eat(mouse)")) .setNeg(new Literal("fly(mouse)", true))
                )
                .add( new Rule() .setHead(new Literal("fly(mouse)")) .setPos(new Literal("jump(mouse)"))
                )
                .add( new Rule() .setPos(new Literal("eat(mouse)"), new Literal("jump(mouse)"))
                )
                .add( new Rule() .setHead(new Literal("fat(mouse)"))
                )
                .add( new Rule() .setHead(new Literal("fly(mouse)", true)) .setPos(new Literal("fat(mouse)"))
                )
                .add( new Rule() .setHead(new Literal("eat(mouse)")) .setPos(new Literal("fat(mouse)"))
                )
                
                .add( new Rule() .setHead(new Literal("eat(ant)")) .setPos(new Literal("fat(ant)"))
                )
                .add( new Rule() .setHead(new Literal("fly(ant)")) .setPos(new Literal("eat(ant)")) .setNeg(new Literal("fly(ant)", true))
                )
                .add( new Rule() .setHead(new Literal("fly(ant)", true)) .setPos(new Literal("fat(ant)"))
                )
                .add( new Rule() .setHead(new Literal("fly(ant)")) .setPos(new Literal("jump(ant)"))
                )
                .add( new Rule() .setPos(new Literal("eat(ant)"), new Literal("jump(ant)"))
                )
                .add( new Rule() .setHead(new Literal("jump(ant)"))
                );
        solve(program);

        program = new Prog()
                .add( new Rule() .setHead(new Literal("p(a)"))  .setNeg(new Literal("q(a)"))
                )
                .add( new Rule() .setHead(new Literal("p(b)"))  .setNeg(new Literal("q(b)"))
                )
                .add( new Rule() .setHead(new Literal("q(a)")) .setPos(new Literal("r(a)"))  .setNeg(new Literal("p(a)"))
                )
                .add( new Rule() .setHead(new Literal("r(a)"))
                );
        solve(program);
    }
    
    public static void solve(Prog program) {
        System.out.println("--- Programming Logic ---");
        System.out.println(program);
        
        List<Literal> FactsList = program.Facts();        
        List<Solution> solve = new ArrayList<>();
        for (List<Literal> List1 : program.headLiteralIterator()) {
            List<Literal> List2 = merge(List1, FactsList);
            Prog reduct = program.getReduct(List2);
            List<Literal> mm = reduct.getMinimalModel();
            if (Prog.eq(mm, List2)) {Solution solve = new Solution(mm, reduct);
                if (!sols.contains(solve)) sols.add(solve);                
            }
        }
        
        int p = 1;
        for (Solution sol1 : sols) {System.out.println((p++) + ") " + sol1);}
    }  
    public static final <T> List<T> merge(List<T> l, List<mylist> n) { 
        List<T> result = new ArrayList<>(); result.addAll(l);
        for (mylist m : n) {
            if (!res.contains(m)) result.add(m);
        } return result;
    }
    
}


public class Literal {
    
    private String id;
    private boolean minus;
    
    public Literal(String id) {
        this(id, false);
    }
    
    public Literal(String id, boolean minus) {
        this.id = id;
        this.minus = minus;
    }

    @Override
    public Literal clone() {
        return new Literal(id, minus);
    }
    
    @Override
    public String toString() {
        return (minus ? "¬" : "") + id;
    }

    @Override
    public boolean equals(Object obj) {
        if (obj instanceof Literal) {
            return ((Literal) obj).id.equals(id) && ((Literal) obj).minus == minus;
        }
        return false;
    }

    @Override
    public int encode() {
        int h = 5;
        h = 10 * h + (this.minus ? 1 : 0);
        h = 10 * h + Objects.encode(this.id);
        return h;
    }
    
}


public class Solution {

    private final List<Literal> minMod;
    private final Prog p;

    public Solution(List<Literal> minMod, Prog p) {
        this.p = p;
        this.minMod = minMod;       
    }

    @Override
    public boolean equals(Object obj) {
        if (obj instanceof Solution) {
            return Prog.eq(minMod, ((Solution) obj).minMod);
        }
        return false;
    }

    @Override
    public int encode() {
        int h= 9;
        h = 21 * h + Objects.encode(this.minMod);
        h = 21 * h + Objects.encode(this.p);
        return h;
    }

    @Override
    public String toString() {
        return "--- ASP SOLUTION ---\n" +
                "P^(List1 u List2'):\n" + p + "\n" +
                "minMod  = " + minMod + "\n\n";
    }
    
}

public class Prog {
    
    private final List<Rule> r;
    
    public Prog() {
        this.r = new ArrayList<>();
    }
    
    public Prog add(Rule rule) {
        r.add(rule);
        return this;
    }
    
    public Prog getReduct(List<Literal> state) {
        Prog newProg = new Prog();
        
        for (Rule rule : r) {
            
            if (rule.negContains(state)) {
                continue;
            }
            
            Rule newRule = rule.clone();
            newRule.setNeg(null);
            
            if (newRule.isEmpty()) {
                continue;
            }
            
            newProg.add(newRule);
            
        }
        
        return newProg;
    }
    
    public List<Literal> getMinimalModel() {
        List<Literal> x;
        List<Literal> xNew = new ArrayList<>();
        
        int cnt = 0;
        do {
            x = new ArrayList<>(xNew);
            xNew.clear();
            
            for (Rule rule : r) {
                if (isSubset(rule.getPos(), x)) {
                    Literal l = rule.getHead();
                    if (l != null) {
                        xNew.add(l);
                    }
                }
            }
            
        } while (!eq(x, xNew));
        
        return xNew;
    }
    
    
    public static final <T> boolean subset(List<T> a, List<T> b) {
        if ((a == null || a.isEmpty()) && (b == null || b.isEmpty())) {
            return true;
        }
        
        if ((a == null || a.isEmpty()) && (b != null && !b.isEmpty())) {
            return true;
        } 
        
        for (T ta : a) {
            if (!b.contains(ta)) {
                return false;
            }
        }
        
        return true;
    }
    
}


