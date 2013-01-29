package view;

import java.awt.AWTEvent;
import java.awt.Toolkit;
import java.awt.datatransfer.Clipboard;
import java.awt.datatransfer.DataFlavor;
import java.awt.datatransfer.StringSelection;
import java.awt.datatransfer.Transferable;
import java.awt.datatransfer.UnsupportedFlavorException;
import java.awt.event.AWTEventListener;
import java.awt.event.KeyEvent;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.JFileChooser;
import javax.swing.ScrollPaneConstants;
import javax.swing.filechooser.FileFilter;
import models.Compilador;
import models.LexicalErrorAdapter;

/**
 *
 * @author Vinícius Ferneda de Lima
 */
public class Principal extends javax.swing.JFrame {

    private JFileChooser chooser = new JFileChooser();
    private File file;
    private static final String EXTENSAO_ARQUIVO = ".txt";
    private boolean anteriorCtrl = false, chooserAberto = false;
    private String conteudo;

    /** Creates new form Principal */
    public Principal() {
        initComponents();
        Toolkit.getDefaultToolkit().addAWTEventListener(new AWTEventListener()   {
            public void eventDispatched(AWTEvent event) {
                if (((KeyEvent) event).getKeyCode() == 119) {
                } else if (((KeyEvent) event).getKeyCode() == 120) {
                } else if (((KeyEvent) event).getKeyCode() == 112) {
                    equipe();
                } else {                                
                    boolean limpaCtrl = false;
                    if (!anteriorCtrl && ((KeyEvent) event).getKeyCode() == 17) {
                        anteriorCtrl = true;
                    } else {
                        limpaCtrl = true;
                    }
                    if (!chooserAberto && anteriorCtrl) {
                        if (((KeyEvent) event).getKeyCode() == 65) {
                            chooserAberto = true;
                            abrir();
                        }
                        if (((KeyEvent) event).getKeyCode() == 78) {
                            chooserAberto = true;
                            novo();
                        }
                        if (((KeyEvent) event).getKeyCode() == 83) {
                            chooserAberto = true;
                            salvar();
                        }
                    }
                    if (limpaCtrl) {
                        anteriorCtrl = false;
                    }
                }
            }

        }, AWTEvent.KEY_EVENT_MASK);
        chooser.setFileFilter(new FileFilter()     {

            @Override
            public boolean accept(File f) {
                return f.getName().toLowerCase().endsWith(EXTENSAO_ARQUIVO) || f.isDirectory();
            }

            @Override
            public String getDescription() {
                return "Formato texto (.txt)";
            }
        });
        chooser.setCurrentDirectory(new File(System.getProperty("user.home")));
        novo();
    }
    
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        jPanel1 = new javax.swing.JPanel();
        jSeparator3 = new javax.swing.JSeparator();
        lbCaminho = new javax.swing.JLabel();
        lbStatus = new javax.swing.JLabel();
        jSplitPane1 = new javax.swing.JSplitPane();
        jScrollPane1 = new javax.swing.JScrollPane();
        txtEditor = new javax.swing.JTextArea();
        jScrollPane1.setVerticalScrollBarPolicy(ScrollPaneConstants.VERTICAL_SCROLLBAR_ALWAYS);
        jScrollPane1.setHorizontalScrollBarPolicy(ScrollPaneConstants.HORIZONTAL_SCROLLBAR_ALWAYS);
        txtEditor.setBorder(new view.BordaNumerada());
        txtEditor.setSize(500, 500);
        jScrollPane2 = new javax.swing.JScrollPane();
        txtMensagem = new javax.swing.JTextArea();
        jScrollPane2.setVerticalScrollBarPolicy(ScrollPaneConstants.VERTICAL_SCROLLBAR_ALWAYS);
        jScrollPane2.setHorizontalScrollBarPolicy(ScrollPaneConstants.HORIZONTAL_SCROLLBAR_ALWAYS);
        tbFerramentas = new javax.swing.JToolBar();
        btNovo = new javax.swing.JButton();
        btNovo.setMnemonic(KeyEvent.VK_CONTROL);
        btAbrir = new javax.swing.JButton();
        tbSalvar = new javax.swing.JButton();
        btCopiar = new javax.swing.JButton();
        btColar = new javax.swing.JButton();
        tbRecortar = new javax.swing.JButton();
        btCompilar = new javax.swing.JButton();
        btGerarCodigo = new javax.swing.JButton();
        btEquipe = new javax.swing.JButton();

        setDefaultCloseOperation(javax.swing.WindowConstants.EXIT_ON_CLOSE);
        setTitle("Compilador");
        setLocationByPlatform(true);
        setMinimumSize(new java.awt.Dimension(810, 100));

        jSeparator3.setOrientation(javax.swing.SwingConstants.VERTICAL);

        lbCaminho.setFont(new java.awt.Font("Arial", 0, 11)); // NOI18N
        lbCaminho.setText("Não salvo");

        lbStatus.setFont(new java.awt.Font("Arial", 0, 11)); // NOI18N
        lbStatus.setText("Não modificado");

        javax.swing.GroupLayout jPanel1Layout = new javax.swing.GroupLayout(jPanel1);
        jPanel1.setLayout(jPanel1Layout);
        jPanel1Layout.setHorizontalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addGap(10, 10, 10)
                .addComponent(lbStatus)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jSeparator3, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(lbCaminho, javax.swing.GroupLayout.DEFAULT_SIZE, 670, Short.MAX_VALUE))
        );
        jPanel1Layout.setVerticalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jSeparator3, javax.swing.GroupLayout.Alignment.TRAILING)
                    .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                        .addComponent(lbStatus)
                        .addComponent(lbCaminho)))
                .addContainerGap())
        );

        jSplitPane1.setOrientation(javax.swing.JSplitPane.VERTICAL_SPLIT);

        jScrollPane1.setMinimumSize(new java.awt.Dimension(27, 300));

        txtEditor.setColumns(20);
        txtEditor.setRows(5);
        txtEditor.setTabSize(4);
        txtEditor.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyReleased(java.awt.event.KeyEvent evt) {
                txtEditorKeyReleased(evt);
            }
        });
        jScrollPane1.setViewportView(txtEditor);

        jSplitPane1.setTopComponent(jScrollPane1);

        jScrollPane2.setMinimumSize(new java.awt.Dimension(27, 100));

        txtMensagem.setColumns(20);
        txtMensagem.setEditable(false);
        txtMensagem.setFont(new java.awt.Font("Courier New", 0, 14)); // NOI18N
        txtMensagem.setRows(5);
        txtMensagem.setAutoscrolls(false);
        jScrollPane2.setViewportView(txtMensagem);

        jSplitPane1.setRightComponent(jScrollPane2);

        tbFerramentas.setFloatable(false);
        tbFerramentas.setRollover(true);

        btNovo.setIcon(new javax.swing.ImageIcon(getClass().getResource("/icons/New.gif"))); // NOI18N
        btNovo.setText("novo [ctrl-n]");
        btNovo.setFocusable(false);
        btNovo.setHorizontalTextPosition(javax.swing.SwingConstants.CENTER);
        btNovo.setVerticalTextPosition(javax.swing.SwingConstants.BOTTOM);
        btNovo.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btNovoActionPerformed(evt);
            }
        });
        tbFerramentas.add(btNovo);

        btAbrir.setIcon(new javax.swing.ImageIcon(getClass().getResource("/icons/open.png"))); // NOI18N
        btAbrir.setText("abrir [ctrl-a]");
        btAbrir.setFocusable(false);
        btAbrir.setHorizontalTextPosition(javax.swing.SwingConstants.CENTER);
        btAbrir.setVerticalTextPosition(javax.swing.SwingConstants.BOTTOM);
        btAbrir.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btAbrirActionPerformed(evt);
            }
        });
        tbFerramentas.add(btAbrir);

        tbSalvar.setIcon(new javax.swing.ImageIcon(getClass().getResource("/icons/save.png"))); // NOI18N
        tbSalvar.setText("salvar [ctrl-s]");
        tbSalvar.setFocusable(false);
        tbSalvar.setHorizontalTextPosition(javax.swing.SwingConstants.CENTER);
        tbSalvar.setVerticalTextPosition(javax.swing.SwingConstants.BOTTOM);
        tbSalvar.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                tbSalvarActionPerformed(evt);
            }
        });
        tbFerramentas.add(tbSalvar);

        btCopiar.setIcon(new javax.swing.ImageIcon(getClass().getResource("/icons/copy.png"))); // NOI18N
        btCopiar.setText("copiar [ctrl-c]");
        btCopiar.setFocusable(false);
        btCopiar.setHorizontalTextPosition(javax.swing.SwingConstants.CENTER);
        btCopiar.setVerticalTextPosition(javax.swing.SwingConstants.BOTTOM);
        btCopiar.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btCopiarActionPerformed(evt);
            }
        });
        tbFerramentas.add(btCopiar);

        btColar.setIcon(new javax.swing.ImageIcon(getClass().getResource("/icons/paste.png"))); // NOI18N
        btColar.setText("colar [ctrl-v]");
        btColar.setFocusable(false);
        btColar.setHorizontalTextPosition(javax.swing.SwingConstants.CENTER);
        btColar.setVerticalTextPosition(javax.swing.SwingConstants.BOTTOM);
        btColar.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btColarActionPerformed(evt);
            }
        });
        tbFerramentas.add(btColar);

        tbRecortar.setIcon(new javax.swing.ImageIcon(getClass().getResource("/icons/cut.png"))); // NOI18N
        tbRecortar.setText("recortar [ctrl-x]");
        tbRecortar.setFocusable(false);
        tbRecortar.setHorizontalTextPosition(javax.swing.SwingConstants.CENTER);
        tbRecortar.setVerticalTextPosition(javax.swing.SwingConstants.BOTTOM);
        tbRecortar.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                tbRecortarActionPerformed(evt);
            }
        });
        tbFerramentas.add(tbRecortar);

        btCompilar.setIcon(new javax.swing.ImageIcon(getClass().getResource("/icons/Properties24.gif"))); // NOI18N
        btCompilar.setText("compilar [F8]");
        btCompilar.setFocusable(false);
        btCompilar.setHorizontalTextPosition(javax.swing.SwingConstants.CENTER);
        btCompilar.setVerticalTextPosition(javax.swing.SwingConstants.BOTTOM);
        btCompilar.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btCompilarActionPerformed(evt);
            }
        });
        tbFerramentas.add(btCompilar);

        btGerarCodigo.setIcon(new javax.swing.ImageIcon(getClass().getResource("/icons/Export24.gif"))); // NOI18N
        btGerarCodigo.setText("gerar código [F9]");
        btGerarCodigo.setFocusable(false);
        btGerarCodigo.setHorizontalTextPosition(javax.swing.SwingConstants.CENTER);
        btGerarCodigo.setVerticalTextPosition(javax.swing.SwingConstants.BOTTOM);
        tbFerramentas.add(btGerarCodigo);

        btEquipe.setIcon(new javax.swing.ImageIcon(getClass().getResource("/icons/Information24.gif"))); // NOI18N
        btEquipe.setText("equipe [F1]");
        btEquipe.setFocusable(false);
        btEquipe.setHorizontalTextPosition(javax.swing.SwingConstants.CENTER);
        btEquipe.setVerticalTextPosition(javax.swing.SwingConstants.BOTTOM);
        btEquipe.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btEquipeActionPerformed(evt);
            }
        });
        tbFerramentas.add(btEquipe);

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addComponent(jPanel1, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addContainerGap())
            .addComponent(jSplitPane1, javax.swing.GroupLayout.DEFAULT_SIZE, 774, Short.MAX_VALUE)
            .addComponent(tbFerramentas, javax.swing.GroupLayout.DEFAULT_SIZE, 774, Short.MAX_VALUE)
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addComponent(tbFerramentas, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jSplitPane1, javax.swing.GroupLayout.DEFAULT_SIZE, 421, Short.MAX_VALUE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jPanel1, javax.swing.GroupLayout.PREFERRED_SIZE, 16, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(0, 0, 0))
        );

        pack();
    }// </editor-fold>//GEN-END:initComponents

    private void btNovoActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btNovoActionPerformed
        novo();
    }//GEN-LAST:event_btNovoActionPerformed

    private void btEquipeActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btEquipeActionPerformed
        equipe();
    }//GEN-LAST:event_btEquipeActionPerformed

    private void btAbrirActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btAbrirActionPerformed
        abrir();
    }//GEN-LAST:event_btAbrirActionPerformed

    private void tbSalvarActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_tbSalvarActionPerformed
        salvar();
    }//GEN-LAST:event_tbSalvarActionPerformed

    private void txtEditorKeyReleased(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_txtEditorKeyReleased
        if (!anteriorCtrl) {
            if (file == null && "".trim().equals(txtEditor.getText())) {
                alteraStatusArquivo(false);
            } else {
                if (conteudo == null || !conteudo.equals(txtEditor.getText())) {
                    alteraStatusArquivo(true);
                }
            }
        }
        conteudo = txtEditor.getText();
    }//GEN-LAST:event_txtEditorKeyReleased

    private void btCopiarActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btCopiarActionPerformed
        copiar();
    }//GEN-LAST:event_btCopiarActionPerformed

    private void btColarActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btColarActionPerformed
        colar();
    }//GEN-LAST:event_btColarActionPerformed

    private void tbRecortarActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_tbRecortarActionPerformed
        recortar();
    }//GEN-LAST:event_tbRecortarActionPerformed

    private void btCompilarActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btCompilarActionPerformed
        compilar();
    }//GEN-LAST:event_btCompilarActionPerformed

    /**
     * @param args the command line arguments
     */
    public static void main(String args[]) {
        java.awt.EventQueue.invokeLater(new Runnable()      {

            public void run() {
                new Principal().setVisible(true);
            }
        });
    }   
    
    private String compilar() {
        String codigoCompilado = null;
        Compilador lexico = new Compilador();
        
        try {     
            codigoCompilado = lexico.compilarLexico(txtEditor.getText());
            txtMensagem.setText(codigoCompilado);
        } catch (LexicalErrorAdapter ex) {
            txtMensagem.setText(ex.getMessage());
        } catch (Exception ex) {
            txtMensagem.setText(ex.getMessage());
        }
        
        return codigoCompilado;
    }
    
    private void abrir() {
        int result = chooser.showOpenDialog(null);
        if (result == JFileChooser.APPROVE_OPTION) {
            defineArquivo(chooser.getSelectedFile());
            FileReader reader = null;
            try {
                reader = new FileReader(chooser.getSelectedFile());
            } catch (FileNotFoundException ex) {
                Logger.getLogger(Principal.class.getName()).log(Level.SEVERE, null, ex);
            }
            BufferedReader leitor = new BufferedReader(reader);
            String linha = "";
            StringBuilder linhas = new StringBuilder();
            try {
                while (leitor.ready()) {
                    linha = leitor.readLine();
                    linhas.append(linha).append("\n");
                }
                leitor.close();
                reader.close();
            } catch (IOException ex) {
                Logger.getLogger(Principal.class.getName()).log(Level.SEVERE, null, ex);
            }
            
            txtEditor.setText(linhas.toString());
            txtMensagem.setText("");
        }
        chooserAberto = false;
    }

    private void salvar() {
        if (file != null) {
            FileWriter fw = null;
            try {
                fw = new FileWriter(file, false);
                fw.write(txtEditor.getText());
            } catch (IOException ex) {
                Logger.getLogger(Principal.class.getName()).log(Level.SEVERE, null, ex);
            } finally {
                try {
                    fw.close();
                } catch (IOException ex) {
                    Logger.getLogger(Principal.class.getName()).log(Level.SEVERE, null, ex);
                }
            }
        } else {
            int result = chooser.showSaveDialog(null);
            if (result == JFileChooser.APPROVE_OPTION) {
                defineArquivo(chooser.getSelectedFile());
                salvar();
            }
        }
        txtMensagem.setText("");
        alteraStatusArquivo(false);
        chooserAberto = false;
    }

    private void novo() {
        txtEditor.setText("");
        txtMensagem.setText("");
        defineArquivo(null);
    }

    private void alteraStatusArquivo(boolean modificado) {
        if (modificado) {
            lbStatus.setText("Modificado");
        } else {
            lbStatus.setText("Não modificado");
        }
    }

    private void defineArquivo(File file) {
        if (file == null) {
            this.file = null;
            lbCaminho.setText("Não salvo");
        } else {
            this.file = file;
            lbCaminho.setText(file.getPath());
        }
        alteraStatusArquivo(false);
    }

    public void copiar() {
        Clipboard clipBoard = getToolkit().getSystemClipboard();
        StringSelection ss = new StringSelection(txtEditor.getSelectedText());
        clipBoard.setContents(ss, ss);
    }

    public void colar() {
        try {
            Clipboard clipBoard = getToolkit().getSystemClipboard();
            Transferable t = clipBoard.getContents(new Object());
            Object texto =  t.getTransferData(DataFlavor.stringFlavor);
            if (texto != null) {
                txtEditor.setText(txtEditor.getText() + texto);
            }
        } catch (UnsupportedFlavorException ex) {
            Logger.getLogger(Principal.class.getName()).log(Level.SEVERE, null, ex);
        } catch (IOException ex) {
            Logger.getLogger(Principal.class.getName()).log(Level.SEVERE, null, ex);
        } catch (Exception e) {}
    }

    public void recortar() {
        copiar();
        String texto = txtEditor.getText();
        txtEditor.setText(texto.substring(0, txtEditor.getSelectionStart()) + texto.substring(txtEditor.getSelectionEnd(), texto.length()));
    }
    
    private void equipe() {
        txtMensagem.setText("Vinícius Ferneda de Lima");
    }

    
    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton btAbrir;
    private javax.swing.JButton btColar;
    private javax.swing.JButton btCompilar;
    private javax.swing.JButton btCopiar;
    private javax.swing.JButton btEquipe;
    private javax.swing.JButton btGerarCodigo;
    private javax.swing.JButton btNovo;
    private javax.swing.JPanel jPanel1;
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JScrollPane jScrollPane2;
    private javax.swing.JSeparator jSeparator3;
    private javax.swing.JSplitPane jSplitPane1;
    private javax.swing.JLabel lbCaminho;
    private javax.swing.JLabel lbStatus;
    private javax.swing.JToolBar tbFerramentas;
    private javax.swing.JButton tbRecortar;
    private javax.swing.JButton tbSalvar;
    private javax.swing.JTextArea txtEditor;
    private javax.swing.JTextArea txtMensagem;
    // End of variables declaration//GEN-END:variables
}
