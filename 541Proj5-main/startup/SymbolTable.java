import java.util.*;
import java.io.*;

class SymbolTable {
	class Scope {
		Map<String, Symb> currentScope;
		Scope next;
		Scope() {
			currentScope = new HashMap<String, Symb>();
			next = null;
		}
		Scope(Scope scopes) {
			currentScope = new HashMap<String, Symb>();
			next = scopes;
		}
	} // class Scope

	private Scope top;

	SymbolTable() {
		top = new Scope();
	}

	public void openScope() {
		top = new Scope(top);
	}

	public void closeScope() throws EmptySTException {
		if (top == null) {
			throw new EmptySTException();
		} else {
			top = top.next;
		}
	} // closeScope()

	public void insert(Symb s)
			throws DuplicateException, EmptySTException {
		final String key = s.name().toLowerCase(Locale.US);
		if (top == null) {
			throw new EmptySTException();
		}
		if (localLookup(key) == null) {
			top.currentScope.put(key,s);
		} else {
			throw new DuplicateException();
		}
	} // insert(Symb)

	public Symb localLookup(String s) {
		final String key = s.toLowerCase(Locale.US);
		if (top == null) {
			return null;
		}
		return top.currentScope.get(key);
	} // localLookup(String)

	public Symb globalLookup(String s) {
		final String key = s.toLowerCase(Locale.US);
		Scope top = this.top;
		while (top != null) {
			final Symb ans = top.currentScope.get(key);
			if (ans == null) {
				top = top.next;
			} else {
				return ans;
			}
		} // while top not null
		return null;
	} // globalLookup(String)

	public String toString() {
		final StringBuilder ans = new StringBuilder();
		Scope top = this.top;
		while (top != null) {
			ans.append(top.currentScope.toString()).append('\n');
			top = top.next;
		}
		return ans.toString();
	} // toString()

	void dump(PrintStream ps) {
		ps.print(toString());
	} // dump(PrintStream)
} // class SymbolTable
