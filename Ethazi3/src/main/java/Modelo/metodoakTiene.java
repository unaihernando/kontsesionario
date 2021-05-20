package Modelo;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;

import javax.swing.JOptionPane;

public class metodoakTiene {

	public static void sartuTiene(ArrayList<Karritoa> karroa, int numTrans, String erabiltzaile, int anyo, int mes, int dia) throws ClassNotFoundException, SQLException {
		for (int i = 0; i < karroa.size(); i++) {
			String elikagaia = karroa.get(i).getElikagaia();
			int kopurua = karroa.get(i).getKopuru();
			double prezioa = karroa.get(i).getBalioa();
			String operazioMota = jasoOperazioMota();
			if (begiratuTiene(elikagaia, numTrans) == false) {
				insertTiene(elikagaia, kopurua, prezioa, operazioMota);
				gehituVende(elikagaia, erabiltzaile, anyo, mes, dia, operazioMota);
			} else {
				updateTiene(elikagaia, kopurua, prezioa);
				gehituVende(elikagaia, erabiltzaile, anyo, mes, dia, operazioMota);
			}
		}
	}

	public static void insertTiene(String elikagaia, int kopurua, double prezioa, String operazioMota) throws ClassNotFoundException, SQLException {
		Connection konekzioa = BBDDKonexioa.getConexion(); 
		String query1 = (Kontsultak.insertTiene + "('" + elikagaia + "', " + (metodoak.jasoTransakzioZbk() - 1) + ", " + kopurua
				+ ", " + prezioa + ", '" + operazioMota + "')");
		try {
			Statement s;
			s = konekzioa.createStatement();
			s.executeUpdate(query1);
		} catch (SQLException e) { 
			e.printStackTrace();
			JOptionPane.showMessageDialog(null, "Datu baseak ezin du gehitu tiene taulari", "ERROR", JOptionPane.ERROR_MESSAGE);
		}
	}

	public static boolean begiratuTiene(String Elikagaia, int numTrans) {
		boolean egia = false;
		Connection konekzioa = BBDDKonexioa.getConexion();
		String query1 = (Kontsultak.selectTiene+"'" + Elikagaia
				+ "' and NumTrans = '" + (numTrans - 1) + "'");
		try {
			ResultSet re;
			PreparedStatement p;
			p = konekzioa.prepareStatement(query1);
			re = p.executeQuery();
			while (re.next()) {
				egia = true;
			}
		} catch (SQLException e) { 
			e.printStackTrace();
			JOptionPane.showMessageDialog(null, "Datu baseak ezin du ikusi tiene taula", "ERROR", JOptionPane.ERROR_MESSAGE);
		}
		return egia;
	}

	public static void updateTiene(String elikagaia, int kopurua, double prezioa) throws ClassNotFoundException, SQLException { 
		Connection konekzioa = BBDDKonexioa.getConexion();
		String query1 = (Kontsultak.updateTiene+"N_Unidades + " + kopurua + ", Precio = Precio + " + prezioa
				+ " where NomProducto = '" + elikagaia + "' and NumTrans = "+(metodoak.jasoTransakzioZbk()-1)+"");
		try {
			Statement s;
			s = konekzioa.createStatement();
			s.executeUpdate(query1);
		} catch (SQLException e) { 
			e.printStackTrace();
			JOptionPane.showMessageDialog(null, "Datu baseak ezin du gehitu produktu berdina", "ERROR", JOptionPane.ERROR_MESSAGE);
		}

	}

	public static void gehituVende(String elikagaia, String erabiltzailea, int anyo, int mes, int dia, String operazioMota) throws ClassNotFoundException, SQLException {
		if(!begiratuProduktuMota(elikagaia).equals("Plato")) {
			if (metodoakKonprobaketak.begiratuStock(elikagaia, metodoakKonprobaketak.konprobatuNIF(erabiltzailea)) < 5) {
				metodoakHornikuntza.sartuHornikuntza(elikagaia, anyo, (mes-1), dia, metodoakKonprobaketak.konprobatuNIF(erabiltzailea), 50);
			}
		}
	}

	public static String jasoOperazioMota () {
		Connection konekzioa = BBDDKonexioa.getConexion();
		String operazioMota = null;
		String query1 = (Kontsultak.selectOperazioMota);
		try {
			ResultSet re;
			PreparedStatement p;
			p = konekzioa.prepareStatement(query1);
			re = p.executeQuery();
			if(re.next()) {
				operazioMota = re.getString("TipoOperacion");
			}
		} catch (SQLException e) { 
			e.printStackTrace();
			JOptionPane.showMessageDialog(null, "Datu baseak ezin du ikusi operazio mota", "ERROR", JOptionPane.ERROR_MESSAGE);
		}
		return operazioMota;
	}

	public static String begiratuProduktuMota (String produktua) {
		Connection konekzioa = BBDDKonexioa.getConexion();
		String produktuMota = null;
		String query1 = (Kontsultak.selectProduktuMota + "'"+produktua+"'");
		try {
			ResultSet re;
			PreparedStatement p;
			p = konekzioa.prepareStatement(query1);
			re = p.executeQuery();
			if(re.next()) {
				produktuMota = re.getString("Tipo");
			}
		} catch (SQLException e) { 
			e.printStackTrace();
			JOptionPane.showMessageDialog(null, "Datu baseak ezin du produktu mota ikusi", "ERROR", JOptionPane.ERROR_MESSAGE);
		}
		return produktuMota;
	}
}
