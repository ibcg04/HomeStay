package ec.edu.espol.ui;

import ec.edu.espol.logica.BuscadorPropiedades;
import ec.edu.espol.logica.HuespedManager;
import ec.edu.espol.modelo.Anfitrion;
import ec.edu.espol.modelo.BaseDatos;
import ec.edu.espol.modelo.Huesped;
import ec.edu.espol.modelo.Propiedad;
import ec.edu.espol.modelo.Unidad;

import javax.swing.DefaultComboBoxModel;
import javax.swing.DefaultListModel;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTabbedPane;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import javax.swing.ListSelectionModel;
import javax.swing.SwingUtilities;
import java.awt.BorderLayout;
import java.awt.FlowLayout;
import java.awt.GridLayout;
import java.util.ArrayList;
import java.util.List;

public class HomeStayGUI extends JFrame {
    private final BaseDatos db = BaseDatos.getDataBase();
    private final DefaultComboBoxModel<Huesped> huespedesModel = new DefaultComboBoxModel<>();
    private final DefaultListModel<UnidadItem> unidadesModel = new DefaultListModel<>();
    private final JTextField nombreHuesped = new JTextField(14);
    private final JTextField idHuesped = new JTextField(7);
    private final JComboBox<Huesped> huespedes = new JComboBox<>(huespedesModel);
    private final JComboBox<String> criterio = new JComboBox<>(new String[]{"Ubicacion", "Precio maximo", "Tipo", "Servicio"});
    private final JTextField valor = new JTextField(14);
    private final JList<UnidadItem> unidades = new JList<>(unidadesModel);
    private final JTextArea reservas = new JTextArea(7, 40);
    private final JTextArea propiedades = new JTextArea();

    private HomeStayGUI() {
        super("HomeStay");
        db.inicializarDatosDemo();

        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(980, 620);
        setLocationRelativeTo(null);
        setLayout(new BorderLayout(8, 8));

        JTabbedPane tabs = new JTabbedPane();
        tabs.addTab("Hospedaje", crearPanelHospedaje());
        tabs.addTab("Propiedades", crearPanelPropiedades());
        add(tabs, BorderLayout.CENTER);

        refrescarTodo();
    }

    public static void mostrar() {
        SwingUtilities.invokeLater(() -> new HomeStayGUI().setVisible(true));
    }

    private JPanel crearPanelHospedaje() {
        JPanel panel = new JPanel(new BorderLayout(10, 10));

        JPanel huespedPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 8, 8));
        huespedPanel.add(new JLabel("Quien se hospeda"));
        huespedPanel.add(nombreHuesped);
        huespedPanel.add(new JLabel("ID"));
        huespedPanel.add(idHuesped);
        huespedPanel.add(boton("Registrar/usar", this::registrarOUsarHuesped));
        huespedPanel.add(new JLabel("Huesped actual"));
        huespedPanel.add(huespedes);

        JPanel busquedaPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 8, 8));
        busquedaPanel.add(new JLabel("Buscar por"));
        busquedaPanel.add(criterio);
        busquedaPanel.add(valor);
        busquedaPanel.add(boton("Buscar", this::buscar));
        busquedaPanel.add(boton("Reservar seleccion", this::reservar));
        busquedaPanel.add(boton("Cancelar reserva actual", this::cancelarReserva));

        JPanel superior = new JPanel(new GridLayout(2, 1));
        superior.add(huespedPanel);
        superior.add(busquedaPanel);

        unidades.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        reservas.setEditable(false);

        panel.add(superior, BorderLayout.NORTH);
        panel.add(new JScrollPane(unidades), BorderLayout.CENTER);
        panel.add(new JScrollPane(reservas), BorderLayout.SOUTH);
        return panel;
    }

    private JPanel crearPanelPropiedades() {
        JPanel panel = new JPanel(new BorderLayout(8, 8));
        propiedades.setEditable(false);
        panel.add(new JScrollPane(propiedades), BorderLayout.CENTER);
        panel.add(boton("Refrescar", this::refrescarTodo), BorderLayout.SOUTH);
        return panel;
    }

    private void registrarOUsarHuesped() {
        int id = leerEntero(idHuesped, "ID");
        String nombre = nombreHuesped.getText().trim();
        if (id < 0 || nombre.isBlank()) {
            avisar("Ingresa nombre e ID del huesped.");
            return;
        }

        Huesped existente = db.buscarHuesped(id);
        if (existente == null) {
            existente = new Huesped(nombre, id);
            db.agregarHuesped(existente);
        }

        nombreHuesped.setText("");
        idHuesped.setText("");
        refrescarTodo();
        huespedes.setSelectedItem(existente);
    }

    private void buscar() {
        unidadesModel.clear();
        String filtro = valor.getText().trim();
        if (filtro.isBlank()) {
            avisar("Ingresa un valor de busqueda.");
            return;
        }

        List<Unidad> resultado;
        try {
            String opcion = (String) criterio.getSelectedItem();
            if ("Ubicacion".equals(opcion)) {
                resultado = BuscadorPropiedades.buscarPorUbicacion(filtro);
            } else if ("Precio maximo".equals(opcion)) {
                resultado = BuscadorPropiedades.buscarPorPrecio(Double.parseDouble(filtro));
            } else if ("Tipo".equals(opcion)) {
                resultado = BuscadorPropiedades.buscarPorTipo(filtro);
            } else {
                resultado = unidadesDisponibles(BuscadorPropiedades.buscarPorServicio(filtro));
            }
        } catch (NumberFormatException ex) {
            avisar("El precio maximo debe ser numerico.");
            return;
        }

        for (Unidad unidad : resultado) {
            unidadesModel.addElement(new UnidadItem(unidad));
        }
        if (unidadesModel.isEmpty()) {
            avisar("No hay unidades disponibles para esa busqueda.");
        }
    }

    private void reservar() {
        Huesped huesped = (Huesped) huespedes.getSelectedItem();
        UnidadItem item = unidades.getSelectedValue();
        if (huesped == null) {
            avisar("Primero registra quien se va a hospedar.");
            return;
        }
        if (item == null) {
            avisar("Selecciona una unidad disponible.");
            return;
        }

        HuespedManager.reservarUnidad(huesped, item.unidad());
        unidadesModel.removeElement(item);
        refrescarTodo();
        avisar("Reserva realizada para " + huesped.getNombre() + ".");
    }

    private void cancelarReserva() {
        Huesped huesped = (Huesped) huespedes.getSelectedItem();
        if (huesped == null) {
            avisar("Selecciona un huesped.");
            return;
        }
        huesped.cancelarReserva();
        refrescarTodo();
    }

    private void refrescarTodo() {
        Huesped seleccionado = (Huesped) huespedes.getSelectedItem();
        huespedesModel.removeAllElements();
        for (Huesped huesped : db.getHuespedes().values()) {
            huespedesModel.addElement(huesped);
        }
        if (seleccionado != null) {
            huespedes.setSelectedItem(seleccionado);
        }
        refrescarReservas();
        refrescarPropiedades();
    }

    private void refrescarReservas() {
        StringBuilder texto = new StringBuilder();
        if (db.getHuespedes().isEmpty()) {
            texto.append("Todavia no hay huesped registrado.\n");
        }
        for (Huesped huesped : db.getHuespedes().values()) {
            texto.append(huesped.getNombre()).append(" (ID ").append(huesped.getID()).append(")\n");
            if (huesped.getUnidadOcupada() == null) {
                texto.append("  Sin reserva activa.\n");
            } else {
                texto.append("  Reserva activa: ").append(describir(huesped.getUnidadOcupada())).append("\n");
            }
            texto.append("  Historial: ").append(huesped.getHistorialReservas().size()).append(" reserva(s)\n\n");
        }
        reservas.setText(texto.toString());
    }

    private void refrescarPropiedades() {
        StringBuilder texto = new StringBuilder();
        for (Anfitrion anfitrion : db.getAnfitriones().values()) {
            texto.append(anfitrion.getNombre()).append(" (ID ").append(anfitrion.getID()).append(")\n");
            for (Propiedad propiedad : anfitrion.getPropiedades()) {
                texto.append("  ").append(propiedad.getUbicacion())
                        .append(" | servicios: ").append(propiedad.getServicios()).append("\n");
                for (Unidad unidad : propiedad.getUnidades()) {
                    texto.append("    - ").append(describir(unidad)).append("\n");
                }
            }
            texto.append("\n");
        }
        propiedades.setText(texto.toString());
    }

    private List<Unidad> unidadesDisponibles(List<Propiedad> propiedades) {
        ArrayList<Unidad> resultado = new ArrayList<>();
        for (Propiedad propiedad : propiedades) {
            for (Unidad unidad : propiedad.getUnidades()) {
                if (unidad.estaDisponible()) {
                    resultado.add(unidad);
                }
            }
        }
        return resultado;
    }

    private String describir(Unidad unidad) {
        String ubicacion = unidad.getPropiedad() == null ? "Sin ubicacion" : unidad.getPropiedad().getUbicacion();
        return unidad.getClass().getSimpleName() + " en " + ubicacion
                + " | $" + unidad.getPrecio()
                + " | " + unidad.getEstadoAlojamiento();
    }

    private JButton boton(String texto, Runnable accion) {
        JButton boton = new JButton(texto);
        boton.addActionListener(event -> accion.run());
        return boton;
    }

    private int leerEntero(JTextField campo, String nombreCampo) {
        try {
            return Integer.parseInt(campo.getText().trim());
        } catch (NumberFormatException ex) {
            avisar(nombreCampo + " debe ser un numero entero.");
            return -1;
        }
    }

    private void avisar(String mensaje) {
        JOptionPane.showMessageDialog(this, mensaje);
    }

    private record UnidadItem(Unidad unidad) {
        @Override
        public String toString() {
            String ubicacion = unidad.getPropiedad() == null ? "Sin ubicacion" : unidad.getPropiedad().getUbicacion();
            return unidad.getClass().getSimpleName() + " en " + ubicacion
                    + " | $" + unidad.getPrecio()
                    + " | " + unidad.getEstadoAlojamiento();
        }
    }
}
