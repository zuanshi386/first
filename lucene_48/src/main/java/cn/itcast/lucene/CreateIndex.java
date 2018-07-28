package cn.itcast.lucene;

import java.io.File;
import java.io.IOException;
import java.nio.file.Paths;

import org.apache.commons.io.FileUtils;
import org.apache.lucene.analysis.Analyzer;
import org.apache.lucene.analysis.standard.StandardAnalyzer;
import org.apache.lucene.document.Document;
import org.apache.lucene.document.Field.Store;
import org.apache.lucene.document.LongPoint;
import org.apache.lucene.document.StoredField;
import org.apache.lucene.document.StringField;
import org.apache.lucene.document.TextField;
import org.apache.lucene.index.IndexWriter;
import org.apache.lucene.index.IndexWriterConfig;
import org.apache.lucene.index.Term;
import org.apache.lucene.search.Query;
import org.apache.lucene.search.TermQuery;
import org.apache.lucene.store.Directory;
import org.apache.lucene.store.FSDirectory;
import org.junit.Test;

import com.hankcs.lucene.HanLPAnalyzer;

public class CreateIndex {

	@Test
	public void createIndex() {
		try {
			// 1.保存数据到索引库需要使用索引的写入对象
			// Directory d 表示索引库的存储路径
			Directory directory = FSDirectory.open(Paths.get("D:\\luceneIndex"));
			// IndexWriterConfig conf 写入对象配置
			Analyzer analyzer = new HanLPAnalyzer();
			IndexWriterConfig config = new IndexWriterConfig(analyzer);
			IndexWriter writer = new IndexWriter(directory, config);

			// 读取数据来源 io流读取磁盘文件
			File fileDir = new File("D:\\searchsource");
			// 获取当前文件夹的所有文件
			File[] files = fileDir.listFiles();
			int i =1;
			for (File file : files) {
				
				// 循环打印所有的文件信息
				System.out.println("当前文件名称为：====" + file.getName());
				System.out.println("当前文件路径为：====" + file.getPath());
				System.out.println("当前文件内容为：====" + FileUtils.readFileToString(file));
				System.out.println("当前文件大小为：====" + FileUtils.sizeOf(file));
				// 每循环一次封装一个document对象
				Document document = new Document();
				/*
				 * StringField 域字段特点：不分词 支持查询 用于存储唯一标识 00112233 TextField 分词存储 支持索引查询 常用类型域字段
				 * LongPoint 数值类型域字段 分词 查询 用于数值的范围查询 不存储在索引库 StroeField 只做数据存储 不支持索引查询
				 * Store.YES/NO 是否存储当前数据
				 */
				
				document.add(new StringField("fileNum", "00000"+i, Store.YES));
				document.add(new TextField("fileName", file.getName(), Store.YES));
				document.add(new StoredField("filePath", file.getPath()));
				document.add(new TextField("fileContent", FileUtils.readFileToString(file), Store.YES));
				document.add(new LongPoint("fileSize", FileUtils.sizeOf(file)));
				// 将当前封装的document文档对象添加到写入对象
				i++;
				writer.addDocument(document);
			}
			writer.commit();
			writer.close();
		} catch (Exception e) {

			e.printStackTrace();
		}
	}

	/*
	 * 删除索引库的数据
	 */
	@Test
	public void deleteIndex() {
		try {
			// 1.保存数据到索引库需要使用索引的写入对象
			// Directory d 表示索引库的存储路径
			Directory directory = FSDirectory.open(Paths.get("D:\\luceneIndex"));
			// IndexWriterConfig conf 写入对象配置
			Analyzer analyzer = new HanLPAnalyzer();
			IndexWriterConfig config = new IndexWriterConfig(analyzer);
			IndexWriter writer = new IndexWriter(directory, config);
			// 删除所有 writer.deleteAll();
			// 根据查询的结果删除数据
			//Query query = new TermQuery(new Term("fileName", "spring"));
			//writer.deleteDocuments(query);
			 writer.deleteAll();

			writer.commit();
			writer.close();
		} catch (Exception e) {

			e.printStackTrace();
		}

	}
	@Test
	public void updateIndex() {
		//修改逻辑是 先删除索引数据 添加document文档数据
		try {
			// 1.保存数据到索引库需要使用索引的写入对象
			// Directory d 表示索引库的存储路径
			Directory directory = FSDirectory.open(Paths.get("D:\\luceneIndex"));
			// IndexWriterConfig conf 写入对象配置
			Analyzer analyzer = new HanLPAnalyzer();
			IndexWriterConfig config = new IndexWriterConfig(analyzer);
			IndexWriter writer = new IndexWriter(directory, config);
		
			Document doc = new Document();
			doc.add(new TextField("fileName","测试修改唯一",Store.YES));
			doc.add(new TextField("fileContext","删除00001的索引",Store.YES));
			writer.updateDocument(new Term("fileNum","000001"), doc);
			writer.commit();
			writer.close();
		} catch (Exception e) {

			e.printStackTrace();
		}

	}
}
