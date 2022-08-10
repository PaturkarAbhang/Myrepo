// ===============================
// Save As: Hotel.java
// Compile As: javac Hotel.java
// Run As: java Hotel
// ===============================

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.sql.*;
import java.util.*;
import javax.swing.event.*;
import javax.swing.table.*;

import java.io.*;
import com.itextpdf.text.Document;
import com.itextpdf.text.pdf.PdfPTable;
import com.itextpdf.text.pdf.PdfPCell;
import com.itextpdf.text.Paragraph;
import com.itextpdf.text.pdf.PdfWriter; 
import com.itextpdf.text.BaseColor;
import com.itextpdf.text.Element; 
import com.itextpdf.text.Font;

class MenuFrame extends JInternalFrame implements ActionListener, ListSelectionListener, InternalFrameListener
{
	private JPanel p1, p2;
	private JTextField txt_search, txt_dish_no, txt_dish_name, txt_dish_rate;
	private JList <String> lst_dishes;
	private JLabel lbl_msg;
	private static Connection con = null;
	private Vector <String> list_data;
	private PreparedStatement ps;
	private String sql;

	public MenuFrame()
	{
		super("Menu Section", true, true, true, true);
		this.addInternalFrameListener(this);

		// left side panel
		p1 = new JPanel();
		p1.setLayout(null);

		JLabel lbl = new JLabel("Search:");
		lbl.setBounds(5, 5, 200, 30);
		p1.add(lbl);

		txt_search = new JTextField();
		txt_search.setBounds(5, 40, 200, 30);
		txt_search.addActionListener(this);
		p1.add(txt_search);

		list_data = new Vector <String>();
		lst_dishes = new JList <String>(list_data);
		JScrollPane sp = new JScrollPane(lst_dishes);
		sp.setBounds(5, 75, 200, 400);
		p1.add(sp);
		lst_dishes.addListSelectionListener(this);


		// Right Side Panel

		p2 = new JPanel();
		p2.setLayout(null);

		lbl = new JLabel("Dish No.");
		lbl.setBounds(5, 5, 150, 30);		
		p2.add(lbl);

		txt_dish_no = new JTextField();
		txt_dish_no.setBounds(160, 5, 150, 30);
		txt_dish_no.setEditable(false);
		p2.add(txt_dish_no);

		lbl	= new JLabel("Dish Name");
		lbl.setBounds(5, 40, 150, 30);
		p2.add(lbl);

		txt_dish_name = new JTextField();
		txt_dish_name.setBounds(160, 40, 150, 30);
		p2.add(txt_dish_name);

		lbl	= new JLabel("Rate");
		lbl.setBounds(5, 75, 150, 30);
		p2.add(lbl);

		txt_dish_rate = new JTextField();
		txt_dish_rate.setBounds(160, 75, 150, 30);
		p2.add(txt_dish_rate);

		String arr[] = {"Insert", "Update", "Delete", "Clear"};

		int x = 5;

		for(int i = 0; i < arr.length; i++)
		{
			JButton b = new JButton(arr[i]);
			b.setBounds(x, 110, 100, 30);
			p2.add(b);
			b.addActionListener(this);
			x += 105;
		}

		lbl_msg = new JLabel("...");
		lbl_msg.setBounds(5, 145, 300, 100);
		p2.add(lbl_msg);

		// SplitPane
		JSplitPane jsp = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT, p1, p2);
		jsp.setDividerLocation(210);
		jsp.setDividerSize(1);
		this.add(jsp, BorderLayout.CENTER);


		getDishes();
		this.setVisible(true);
		this.setSize(750, 500);
	}

	@Override
	public void actionPerformed(ActionEvent e)
	{
		try
		{
			Object obj = e.getSource();

			if(obj == txt_search)
			{				
				list_data.clear();
				String search_pattern = txt_search.getText().trim().toUpperCase();
				sql = "select dish_name from dishes where dish_name like '"+search_pattern+"%'";								
				connect();
				ps = con.prepareStatement(sql);
				ResultSet rs = ps.executeQuery();
				while(rs.next())
				{
					list_data.add(rs.getString("dish_name"));
				}
				rs.close();
				ps.close();
				disconnect();
				lst_dishes.setListData(list_data);
			}
		}
		catch(Exception ex)
		{
			JOptionPane.showMessageDialog(this, ex);
		}

		try
		{
			lbl_msg.setText("");
			String cmd = e.getActionCommand().toUpperCase();

			switch(cmd)
			{
				case "INSERT": 
					String dish_name = txt_dish_name.getText().trim().toUpperCase();
					double dish_rate = Double.parseDouble(txt_dish_rate.getText());

					sql = "insert into dishes (dish_name, dish_rate) values (?, ?)";								
					connect();					
					ps = con.prepareStatement(sql);
					ps.setString(1, dish_name);
					ps.setDouble(2, dish_rate);
					int n = ps.executeUpdate();
					ps.close();
					disconnect();
					if(n == 1)
					{
						getDishes();
						lbl_msg.setForeground(Color.GREEN);
						lbl_msg.setText("Record inserted..");
					}
					else
					{
						lbl_msg.setForeground(Color.RED);
						lbl_msg.setText("Opps!! Unable to insert record..");
					}
					break;

				case "UPDATE": 
					int dno = Integer.parseInt(txt_dish_no.getText());
					String dn = txt_dish_name.getText().trim().toUpperCase();
					double dr = Double.parseDouble(txt_dish_rate.getText());

					sql = "update dishes set dish_name = ?, dish_rate = ? where dish_no= ?";
					connect();
					ps = con.prepareStatement(sql);
					ps.setString(1, dn);
					ps.setDouble(2, dr);
					ps.setInt(3, dno);
					int x = ps.executeUpdate();
					ps.close();
					disconnect();

					if(x == 1)
					{
						getDishes();
						lbl_msg.setForeground(Color.GREEN);
						lbl_msg.setText("Record updated..");
					}
					else
					{
						lbl_msg.setForeground(Color.RED);
						lbl_msg.setText("Record not updated..");
					}
					break;

				case "DELETE": 
					String dish_num = txt_dish_no.getText().trim();
					if(dish_num.length() > 0)
					{
						int dnum = Integer.parseInt(txt_dish_no.getText());
						sql = "delete from dishes where dish_no = ?";
						connect();
						ps = con.prepareStatement(sql);
						ps.setInt(1, dnum);
						int y = ps.executeUpdate();
						ps.close();
						disconnect();

						if(y == 1)
						{
							lbl_msg.setForeground(Color.GREEN);
							lbl_msg.setText("Deleted..");
							getDishes();
							txt_dish_no.setText("");
							txt_dish_name.setText("");
							txt_dish_rate.setText("");
						}
						else
						{
							lbl_msg.setForeground(Color.RED);
							lbl_msg.setText("Not Deleted..");
						}
					}
					else
					{
						lbl_msg.setForeground(Color.RED);
						lbl_msg.setText("Dish not selected..");
					}
					break;
				case "CLEAR": 
					getDishes();
					txt_dish_no.setText("");
					txt_dish_name.setText("");
					txt_dish_rate.setText("");
					lbl_msg.setText("");
					break;
			}
		}
		catch(Exception ex)
		{
			JOptionPane.showMessageDialog(this, ex);
		}
	}

	@Override
	public void valueChanged(ListSelectionEvent e)
	{
		try
		{
			Object obj = e.getSource();

			if(obj == lst_dishes)
			{
				String sel_dish = (String)lst_dishes.getSelectedValue();
				sql = "select * from dishes where dish_name = ?";
				connect();
				ps = con.prepareStatement(sql);
				ps.setString(1, sel_dish);
				ResultSet rs = ps.executeQuery();
				if(rs.next())
				{
					int dish_no = rs.getInt("dish_no");
					String dish_name = rs.getString("dish_name");
					double dish_rate = rs.getDouble("dish_rate");

					txt_dish_no.setText(dish_no+"");
					txt_dish_name.setText(dish_name);
					txt_dish_rate.setText(dish_rate+"");
				}
				rs.close();
				ps.close();
				disconnect();
			}
		}
		catch(Exception ex)
		{
			JOptionPane.showMessageDialog(this, ex);
		}
	}

	public void connect()
	{
		try
		{
			Class.forName("com.mysql.cj.jdbc.Driver");
			con = DriverManager.getConnection("jdbc:mysql://localhost:3306/hoteldb", "root", "");
		}
		catch(Exception ex)
		{
			JOptionPane.showMessageDialog(this, ex);
		}
	}

	public void disconnect()
	{
		try
		{
			if(!con.isClosed())
			con.close();
		}
		catch(Exception ex)
		{
			JOptionPane.showMessageDialog(this, ex);
		}
	}

	public void getDishes()
	{
		try
		{
			list_data.clear();
			sql = "select dish_name from dishes";
			connect();
			ps = con.prepareStatement(sql);
			ResultSet rs = ps.executeQuery();

			while(rs.next())
			{
				String dish_name = rs.getString("dish_name");
				list_data.add(dish_name);
			}

			rs.close();
			ps.close();
			disconnect();

			lst_dishes.setListData(list_data);
		}
		catch(Exception ex)
		{
			JOptionPane.showMessageDialog(this, ex);
		}
	}

	public void internalFrameOpened(InternalFrameEvent e){}
	public void internalFrameActivated(InternalFrameEvent e){}
	public void internalFrameDeactivated(InternalFrameEvent e){}
	public void internalFrameIconified(InternalFrameEvent e){}
	public void internalFrameDeiconified(InternalFrameEvent e){}
	public void internalFrameClosing(InternalFrameEvent e){}

	public void internalFrameClosed(InternalFrameEvent e)
	{		
		MyFrame.f1 = null;
	}	
}

class BillFrame extends JInternalFrame implements ActionListener, ListSelectionListener, InternalFrameListener
{
	private JPanel p1, p2;
	private JTextField txt_search;
	private Vector <String> list_data;
	private JList <String> lst_dishes;
	private JTextField txt_table_no;
	private JTextField txt_bill_date;
	private JTextField txt_dish_name;
	private JTextField txt_dish_rate;
	private JTextField txt_quantity;
	private JTable tbl_bill_details;
	private DefaultTableModel dtm;
	private Vector <String> column_header;
	private Vector <Vector> rows;
	private JTextField txt_total_bill;
	private Connection con;
	private PreparedStatement ps;
	private String sql;

	public BillFrame()
	{
		super("Billing Section", true, true, true,true);
		this.addInternalFrameListener(this);

		// left side panel
		p1 = new JPanel();
		p1.setLayout(null);

		JLabel lbl = new JLabel("Search:");
		lbl.setBounds(5, 5, 200, 30);
		p1.add(lbl);

		txt_search = new JTextField();
		txt_search.setBounds(5, 40, 200, 30);
		p1.add(txt_search);
		txt_search.addActionListener(this);

		list_data = new Vector <String>();
		lst_dishes = new JList <String>(list_data);
		JScrollPane sp = new JScrollPane(lst_dishes);
		sp.setBounds(5, 75, 200, 400);
		p1.add(sp);
		lst_dishes.addListSelectionListener(this);

		// Right Side Panel

		p2 = new JPanel();
		p2.setLayout(null);

		JLabel lbl_title = new JLabel("New Bill", JLabel.CENTER);
		lbl_title.setFont(new java.awt.Font("Gabriola", java.awt.Font.BOLD, 30));
		lbl_title.setForeground(Color.RED);
		lbl_title.setBounds(100, 10, 150, 60);
		p2.add(lbl_title);

		lbl = new JLabel("Table No.");
		lbl.setBounds(5, 75, 150, 30);
		p2.add(lbl);

		txt_table_no = new JTextField();
		txt_table_no.setBounds(160, 75, 50, 30);
		p2.add(txt_table_no);

		lbl = new JLabel("Date");
		lbl.setBounds(215, 75, 50, 30);
		p2.add(lbl);

		txt_bill_date = new JTextField();
		txt_bill_date.setBounds(270, 75, 150, 30);
		p2.add(txt_bill_date);
		txt_bill_date.setEditable(false);

		java.util.Date today_date = new java.util.Date();
		txt_bill_date.setText(today_date.toString());

		lbl = new JLabel("Dish Name");
		lbl.setBounds(5, 110, 150, 30);
		p2.add(lbl);

		txt_dish_name = new JTextField();
		txt_dish_name.setBounds(160, 110, 150, 30);
		p2.add(txt_dish_name);
		txt_dish_name.setEditable(false);

		lbl = new JLabel("Dish Rate");
		lbl.setBounds(5, 145, 150, 30);
		p2.add(lbl);

		txt_dish_rate = new JTextField("0");
		txt_dish_rate.setBounds(160, 145, 150, 30);
		p2.add(txt_dish_rate);
		txt_dish_rate.setEditable(false);

		lbl = new JLabel("Quantity");
		lbl.setBounds(5, 180, 150, 30);
		p2.add(lbl);

		txt_quantity = new JTextField();
		txt_quantity.setBounds(160, 180, 150, 30);
		p2.add(txt_quantity);
		txt_quantity.addActionListener(this);		

		lbl = new JLabel("Bill Details:");
		lbl.setBounds(5, 250, 100, 30);
		p2.add(lbl);
		
		String arr[] = {"Dish Name", "Dish Rate", "Quantity", "Amount"};
		column_header = new Vector <String>();
		for(String col: arr)
		column_header.add(col);

		rows = new Vector <Vector>();		

		dtm = new DefaultTableModel(rows, column_header);
		tbl_bill_details = new JTable(dtm);
		JScrollPane scroll = new JScrollPane(tbl_bill_details);
		scroll.setBounds(5, 285, 500, 150);
		p2.add(scroll);

		JTableHeader th = tbl_bill_details.getTableHeader();
		th.setBackground(Color.WHITE);
		th.setForeground(Color.BLACK);

		lbl = new JLabel("Total Bill Amount");
		lbl.setBounds(5, 440, 150, 30);
		p2.add(lbl);

		txt_total_bill = new JTextField("0");
		txt_total_bill.setBounds(160, 440, 100, 30);
		p2.add(txt_total_bill);
		txt_total_bill.setEditable(false);

		JButton b = new JButton("Save & Print");
		b.setActionCommand("SAVE_AND_PRINT");
		b.setBounds(270, 440, 150, 30);
		p2.add(b);
		b.addActionListener(this);

		JSplitPane jsp = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT, p1, p2);
		this.add(jsp, BorderLayout.CENTER);
		jsp.setDividerLocation(210);
		jsp.setDividerSize(2);

		getDishes();
		this.setVisible(true);
		this.setSize(750, 550);
	}

	@Override
	public void actionPerformed(ActionEvent e)
	{
		try
		{
			Object obj = e.getSource();

			if(obj == txt_search)
			{
				list_data.clear();
				String search_pattern = txt_search.getText().trim().toUpperCase();
				sql = "select dish_name from dishes where dish_name like '"+search_pattern+"%'";			
				connect();
				ps = con.prepareStatement(sql);
				ResultSet rs = ps.executeQuery();

				while(rs.next())
				{
					String dish_name = rs.getString("dish_name");
					list_data.add(dish_name);
				}

				rs.close();
				ps.close();
				disconnect();

				lst_dishes.setListData(list_data);
			}
			else
			if(obj == txt_quantity)
			{
				String drate = txt_dish_rate.getText().trim();
				String qty = txt_quantity.getText().trim();
				txt_quantity.setText("0");
				if(drate.length() > 0 && qty.length() > 0)
				{
					double rate = Double.parseDouble(drate);
					double quantity = Double.parseDouble(qty);

					if(rate > 0 && quantity > 0)
					{
						double amount = rate * quantity;

						Vector <String> single_row = new Vector <String>();
						String dish_name = txt_dish_name.getText().trim().toUpperCase();
						single_row.add(dish_name);
						single_row.add(rate+"");
						single_row.add(quantity+"");
						single_row.add(amount+"");
						rows.add(single_row);
						dtm.setDataVector(rows, column_header);
						tbl_bill_details.setModel(dtm);

						double total_bill = 0;
						total_bill = Double.parseDouble(txt_total_bill.getText().trim());
						total_bill = total_bill + amount;
						txt_total_bill.setText(total_bill+"");
					}
				}				
			}

			String cmd = e.getActionCommand().toUpperCase();

			if(cmd.equalsIgnoreCase("SAVE_AND_PRINT"))
			{
				String table_no = txt_table_no.getText().trim().toUpperCase();				
				String bill_date =  txt_bill_date.getText().trim().toUpperCase();
				double total_bill_amount = Double.parseDouble(txt_total_bill.getText().trim());

				if(table_no.length() > 0)
				{
					if(total_bill_amount > 0)
					{
						sql = "insert into bills (bill_date, table_no, total_bill) values(?, ?, ?)";						
						connect();
						ps = con.prepareStatement(sql);
						ps.setString(1, bill_date);
						ps.setString(2, table_no);
						ps.setDouble(3, total_bill_amount);
						int n = ps.executeUpdate();
						ps.close();

						sql = "select max(bill_no) from bills";
						ps = con.prepareStatement(sql);
						ResultSet rs = ps.executeQuery();

						int bill_no = 0;

						if(rs.next())
						{
							bill_no = rs.getInt(1);
						}
						rs.close();
						ps.close();												

						sql = "insert into bill_details values(?, ?, ?, ?, ?, ?, ?)";
						ps = con.prepareStatement(sql);

						for(Vector <String>one_row : rows)
						{
							String dish_name = one_row.get(0);
							double dish_rate = Double.parseDouble(one_row.get(1));
							double quantity = Double.parseDouble(one_row.get(2));
							double amount = Double.parseDouble(one_row.get(3));

							ps.setInt(1, bill_no);
							ps.setString(2, bill_date);
							ps.setString(3, table_no);
							ps.setString(4, dish_name);
							ps.setDouble(5, dish_rate);
							ps.setDouble(6, quantity);
							ps.setDouble(7, amount);
							int x = ps.executeUpdate();							
						}
						ps.close();
						disconnect();

						// creating pdf of bill

						Document doc = new Document();
            			PdfWriter w = PdfWriter.getInstance(doc, new FileOutputStream("bills_pdf/"+bill_no+".pdf"));						            		
	           			doc.open();

	           			// Title	           			
	           			Font f = new Font(Font.FontFamily.HELVETICA, 18, Font.BOLD, BaseColor.RED);								
	           			// Paragraph p = new Paragraph("              *** BILL ***             ", f);
	           			Paragraph p = new Paragraph("*** BILL ***", f);
	           			p.setAlignment(1);
	           			/*
	           			Alignment 
	           			0 = left
	           			1 = center
	           			2 = right
	           			*/
	           			doc.add(p);

	           			//
	           			doc.add(new Paragraph("Table Number: "+table_no));
	           			doc.add(new Paragraph("Bill Number: "+bill_no));
	           			doc.add(new Paragraph("Bill Date: "+bill_date));

	           			doc.add(new Paragraph("\n\n"));

	           			doc.add(new Paragraph("Bill Details:"));

	           			PdfPTable bill_table = new PdfPTable(4);
            			bill_table.setWidthPercentage(100);
            			bill_table.setSpacingBefore(11f);
            			bill_table.setSpacingAfter(11f);

            			float col_width[] = {3f, 2f, 2f, 2f};
            			bill_table.setWidths(col_width);

            			String cols[] = {"Dish Name", "Dish Rate", "Quantity", "Amount"};

            			for(int i = 0; i < cols.length; i++)
            			{
                			PdfPCell c = new PdfPCell(new Paragraph(cols[i], new Font(Font.FontFamily.TIMES_ROMAN, 11, Font.BOLD, BaseColor.BLACK)));
                			c.setHorizontalAlignment(Element.ALIGN_CENTER);       
                			bill_table.addCell(c);
            			}

            			for(Vector <String> single_row : rows)
            			{
            				for(Object item : single_row)
            				{
            					PdfPCell c = new PdfPCell(new Paragraph((String)item));
                    			c.setHorizontalAlignment(Element.ALIGN_CENTER);       
                    			bill_table.addCell(c);
            				}
            			}

            			doc.add(bill_table);        
            			p = new Paragraph("Total Bill Amount: "+total_bill_amount+" Rs.");
            			p.setAlignment(2);
            			doc.add(p);

            			p = new Paragraph("*** Thank You!!! ***");
            			p.setAlignment(1);
            			doc.add(p);

            			p = new Paragraph("*** Visit Again!!!***");
            			p.setAlignment(1);
            			doc.add(p);

            			doc.close();
           	 			w.close();

						JOptionPane.showMessageDialog(this, "Bill Saved..");
					}
					else
					JOptionPane.showMessageDialog(this, "Items not seleted..");
				}
				else
				{
					JOptionPane.showMessageDialog(this, "Table number is required");
				}
			}
		}
		catch(Exception ex)
		{
			JOptionPane.showMessageDialog(this, ex);
		}
	}

	@Override
	public void valueChanged(ListSelectionEvent e)
	{
		try
		{
			String sel_dish = lst_dishes.getSelectedValue().toString();
			sql = "select * from dishes where dish_name = ?";
			connect();
			ps = con.prepareStatement(sql);
			ps.setString(1, sel_dish);
			ResultSet rs = ps.executeQuery();

			if(rs.next())
			{
				int dish_no = rs.getInt("dish_no");
				String dish_name = rs.getString("dish_name");
				double dish_rate = rs.getDouble("dish_rate");
				txt_dish_name.setText(dish_name);
				txt_dish_rate.setText(dish_rate+"");
			}
			rs.close();
			ps.close();
			disconnect();

		}
		catch(Exception ex)
		{
			JOptionPane.showMessageDialog(this, ex);
		}
	}

	public void connect()
	{
		try
		{
			Class.forName("com.mysql.cj.jdbc.Driver");
			con = DriverManager.getConnection("jdbc:mysql://localhost:3306/hoteldb", "root", "");
		}
		catch(Exception ex)
		{
			JOptionPane.showMessageDialog(this, ex);
		}
	}

	public void disconnect()
	{
		try
		{
			if(!con.isClosed())
			con.close();
		}
		catch(Exception ex)
		{
			JOptionPane.showMessageDialog(this, ex);
		}
	}

	public void getDishes()
	{
		try
		{
			list_data.clear();
			sql = "select dish_name from dishes";
			connect();
			ps = con.prepareStatement(sql);
			ResultSet rs = ps.executeQuery();

			while(rs.next())
			{
				String dish_name = rs.getString("dish_name");
				list_data.add(dish_name);
			}

			rs.close();
			ps.close();
			disconnect();

			lst_dishes.setListData(list_data);
		}
		catch(Exception ex)
		{
			JOptionPane.showMessageDialog(this, ex);
		}
	}

	public void internalFrameOpened(InternalFrameEvent e){}
	public void internalFrameActivated(InternalFrameEvent e){}
	public void internalFrameDeactivated(InternalFrameEvent e){}
	public void internalFrameIconified(InternalFrameEvent e){}
	public void internalFrameDeiconified(InternalFrameEvent e){}
	public void internalFrameClosing(InternalFrameEvent e){}

	public void internalFrameClosed(InternalFrameEvent e)
	{		
		MyFrame.f2 = null;
	}	
}

class MyFrame extends JFrame implements ActionListener
{
	private JDesktopPane jdp;
	private JToolBar tbar;
	public static MenuFrame f1 = null;
	public static BillFrame f2 = null;
	
	public MyFrame()
	{
		super("Simple Hotel Billing Management");

		jdp = new JDesktopPane();
		this.add(jdp, BorderLayout.CENTER);

		tbar = new JToolBar();
		this.add(tbar, BorderLayout.NORTH);
		tbar.setFloatable(false);

		String arr[] = {"Menu Section", "Billing Section"};

		for(int i = 0; i < arr.length; i++)
		{
			JButton b = new JButton(arr[i]);
			tbar.add(b);
			b.addActionListener(this);
		}

		this.setVisible(true);
		this.setSize(900, 650);
	}

	@Override
	public void actionPerformed(ActionEvent e)
	{
		String cmd = e.getActionCommand().toUpperCase();
		switch(cmd)
		{
			case "MENU SECTION":
				if(f1 == null)
				{
					f1 = new MenuFrame();
					jdp.add(f1);
				}
				break;
			case "BILLING SECTION":
				if(f2 == null)
				{
					f2 = new BillFrame();
					jdp.add(f2);
				}
				break;
		}
	}
}

class Hotel
{
	public static void main(String args[])
	{
		MyFrame f = new MyFrame();
		f.setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
	}
}

