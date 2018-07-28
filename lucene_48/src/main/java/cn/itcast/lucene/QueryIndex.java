package cn.itcast.lucene;

import java.io.IOException;
import java.nio.file.Paths;

import org.apache.lucene.document.Document;
import org.apache.lucene.document.LongPoint;
import org.apache.lucene.index.DirectoryReader;
import org.apache.lucene.index.IndexReader;
import org.apache.lucene.index.Term;
import org.apache.lucene.queryparser.classic.MultiFieldQueryParser;
import org.apache.lucene.queryparser.classic.QueryParser;
import org.apache.lucene.search.BooleanClause;
import org.apache.lucene.search.BooleanQuery;
import org.apache.lucene.search.BooleanClause.Occur;
import org.apache.lucene.search.IndexSearcher;
import org.apache.lucene.search.MatchAllDocsQuery;
import org.apache.lucene.search.Query;
import org.apache.lucene.search.ScoreDoc;
import org.apache.lucene.search.TermQuery;
import org.apache.lucene.search.TopDocs;
import org.apache.lucene.store.FSDirectory;
import org.junit.Test;

import com.hankcs.lucene.HanLPAnalyzer;

public class QueryIndex {
	
	//查询索引库的所有文件数据
	@Test
	public void queryAllIndex() {
		try {
			Query queryAll  = new MatchAllDocsQuery();
			
			doQuery(queryAll);
		} catch (Exception e) {
			
			e.printStackTrace();
		}
	}
	//查询索引库的所有文件数据
	@Test
	public void queryByTerm() {
		try {
			//创建支持词条查询的对象
			//创建词条对象查询域为fileName 词条为spring
			Query queryTerm  = new TermQuery(new Term("fileName","spring"));
			
			doQuery(queryTerm);
		} catch (Exception e) {
			
			e.printStackTrace();
		}
	}
	//查询索引库的文档大小在1---50字节长度的文档
	@Test
	public void queryBySize() {
		try {
			//创建支持词条查询的对象
			//lowerValue 1  upperValue 50
			Query querySize  = LongPoint.newRangeQuery("fileSize", 1l, 50l);
			doQuery(querySize);
		} catch (Exception e) {
			
			e.printStackTrace();
		}
	}
	//组合方式查询
	@Test
	public void queryByBoolean() {
		try {
			//创建第一个查询对象
			Query queryTerm1  = new TermQuery(new Term("fileName","传智播客"));
			Query queryTerm2  = new TermQuery(new Term("fileName","不明觉厉"));
			//子查询的语句对象指定查询对象的限制
			/*
			 * MUST表示必须匹配
			 * MUST_NOT 不能匹配
			 * SHOULD 可以有
			 * */
			BooleanClause bc1 = new BooleanClause(queryTerm1, Occur.MUST);
			BooleanClause bc2 = new BooleanClause(queryTerm2, Occur.SHOULD);
			//使用限定查询条件的对象 bc1 bc2组装查询
			BooleanQuery queryBoolean = new BooleanQuery.Builder().add(bc1).add(bc2).build();
			doQuery(queryBoolean);
		} catch (Exception e) {
			
			e.printStackTrace();
		}
	}

	//单域字段分词查询
	@Test
	public void queryByStr() {
		try {
			//1.定义关键字作为查询
			String queryStr ="传智播客优秀企业";
			//2.创建解析字符串的对象
			//参数1是解析查询的域字段
			QueryParser parser = new QueryParser("fileName", new HanLPAnalyzer());
			Query query =parser.parse(queryStr);
			doQuery(query);
		} catch (Exception e) {
			
			e.printStackTrace();
		}
	}
	   //多域字段分词查询
		@Test
		public void queryByMultiFiled() {
			try {
				//1.定义关键字作为查询
				String queryStr ="传智播客优秀企业";
				//2.创建解析字符串的对象
				//参数1是解析查询的域字段
				String [] files = new String [] {"fileName","fileContent"};
				MultiFieldQueryParser parser = 
						new MultiFieldQueryParser(files, new HanLPAnalyzer());
				Query query =parser.parse(queryStr);
				doQuery(query);
			} catch (Exception e) {
				
				e.printStackTrace();
			}
		}

	//通用的查询方法
	private void doQuery(Query queryAll) throws IOException {
		//创建索引的读取对象
		IndexReader reader = DirectoryReader.open(FSDirectory.open(Paths.get("D:\\luceneIndex")));
		//通过IndexSearcher对象执行查询
		IndexSearcher searcher = new IndexSearcher(reader);
		//读取所有的查询对象创建
		
		//searcher执行query查询数据
		TopDocs topDocs = searcher.search(queryAll,100);
		//topDocs对象是查询结果包含 文档id的数组 和每个文档的得分 
		//totalHits 文档的总命中数量
		System.out.println("文档的总命中数量为：=="+topDocs.totalHits);
		//查询文档id的数组 和每个文档的得分 
		ScoreDoc[] scoreDocs = topDocs.scoreDocs;
		for(ScoreDoc sd:scoreDocs) {
			System.out.println("当前文档id==="+sd.doc);
			System.out.println("当前文档的得分为==="+sd.score);
			//通过文档的id提取文档的数据
			Document document = searcher.doc(sd.doc);
			System.out.println("文档的name为：==="+document.get("fileName"));
			System.out.println("文档的path为：==="+document.get("filePath"));
			System.out.println("文档的大小为：==="+document.get("fileSize"));
			
		}
	}

}
