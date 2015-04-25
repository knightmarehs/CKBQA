package structs;

public class Word implements Comparable<Word> {
	public String baseForm = null;
	public String originalForm = null;
	public String posTag = null;
	public int position = -1;	// 句子中第一个词的position是1. 亦可参见输出的语法树结点后面[]中的数字.
	public String key = null;
	
	public boolean isCovered = false;
	public boolean isIgnored = false;
	
	public String ner = null;	// 记录ner的结果，不是ne的为null
	public Word nnNext = null;	// 记录nn修饰词的后一个词，终止以null结束
	public Word nnPrev = null;	// 记录nn修饰词的前一个词，终止以null结束
	public Word crr	= null;		// 记录指代消解的指向，只记录在短语的head上，指向另一个短语的head
	
	public Word (String base, String original, String pos, int posi) {
		baseForm = base;
		originalForm = original;
		posTag = pos;
		position = posi;		
		key = new String(originalForm+"["+position+"]");
	}
	
	@Override
	public String toString() {
		return key;
	}

	public int compareTo(Word another) {
		return this.position-another.position;
	}
	
	@Override
	public int hashCode() {
		return key.hashCode();
	}
	
	@Override
	public boolean equals(Object o) {
		return (o instanceof Word) 
			&& originalForm.equals(((Word)o).originalForm)
			&& position == ((Word)o).position;
	}
	
	// 小心NnHead不是nn结构的顶点
	public Word getNnHead() {
		Word w = this;
		while (w.nnPrev != null) {
			w = w.nnPrev;
		}
		return w;
	}
	
	public String getFullEntityName() {
		Word w = this.getNnHead();
		StringBuilder sb = new StringBuilder("");
		while (w != null) {
			sb.append(w.originalForm);			
			sb.append(' ');
			w = w.nnNext;
		}
		sb.deleteCharAt(sb.length()-1);
		return sb.toString();
	}
	
	public String getBaseFormEntityName() {
		Word w = this.getNnHead();
		StringBuilder sb = new StringBuilder("");
		while (w != null) {
			sb.append(w.baseForm);
			sb.append(' ');
			w = w.nnNext;
		}
		sb.deleteCharAt(sb.length()-1);
		return sb.toString();
	}
	
	public String isNER () {
		return this.getNnHead().ner;
	}
	
	public void setIsCovered () {
		Word w = this.getNnHead();
		while (w != null) {
			w.isCovered = true;
			w = w.nnNext;
		}
	}	
}
