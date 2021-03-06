package br.com.caelum.cadastro;

import android.app.AlertDialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.net.Uri;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.view.ContextMenu;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ListView;
import android.widget.Toast;

import java.util.List;

import br.com.caelum.cadastro.adapter.ListaAlunosAdapter;
import br.com.caelum.cadastro.dao.AlunoDAO;
import br.com.caelum.cadastro.modelo.Aluno;
import br.com.caelum.cadastro.task.EnviaContatosTask;


public class ListaAlunosActivity extends ActionBarActivity {

    /*
        Variavel responsavel por ficar ouvindo a bateria.
    */
    private BroadcastReceiver bateria = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            int valor = intent.getIntExtra("level", 0);
            Toast.makeText(context, valor + "%", Toast.LENGTH_SHORT).show();
        }
    };


    private ListView listaAlunos;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_lista_alunos);

        this.listaAlunos = (ListView) findViewById(R.id.lista_alunos);

        this.listaAlunos.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                // Editar o aluno
                Aluno alunoSelecionado = (Aluno)parent.getItemAtPosition(position);
                Intent edicao = new Intent(ListaAlunosActivity.this, FormularioActivity.class);
                edicao.putExtra(FormularioActivity.ALUNO_SELECIONADO, alunoSelecionado);
                startActivity(edicao);
            }
        });

        this.listaAlunos.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
            @Override
            public boolean onItemLongClick(AdapterView<?> parent, View view, int position, long id) {
                return false; // return false nao chama o click simples
            }
        });

        registerForContextMenu(this.listaAlunos);

        Button botaoAdiciona = (Button)findViewById(R.id.lista_alunos_floating_button);
        botaoAdiciona.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(ListaAlunosActivity.this, FormularioActivity.class);
                startActivity(i);
            }
        });
    }

    @Override
    protected void onPause() {
        super.onPause();

        unregisterReceiver(bateria);
    }

    @Override
    protected void onResume(){
        super.onResume();
        registerReceiver(bateria, new IntentFilter(Intent.ACTION_BATTERY_CHANGED));

        this.carregaLista();
    }

    protected void carregaLista(){
        AlunoDAO dao = new AlunoDAO(this);
        List<Aluno> alunos = dao.getLista();
        dao.close();

        ListaAlunosAdapter adapter = new ListaAlunosAdapter(this, alunos);
        this.listaAlunos.setAdapter(adapter);
    }

    @Override
    public void onCreateContextMenu(ContextMenu menu, View v, ContextMenu.ContextMenuInfo menuInfo) {

        AdapterView.AdapterContextMenuInfo contextInfo = (AdapterView.AdapterContextMenuInfo) menuInfo;
        final Aluno alunoSelecionado = (Aluno)this.listaAlunos.getAdapter().getItem(contextInfo.position);

        Intent intentLigar = new Intent(Intent.ACTION_CALL);
        intentLigar.setData(Uri.parse("tel:" + alunoSelecionado.getTelefone()));
        MenuItem ligar = menu.add("Ligar");
        ligar.setIntent(intentLigar);

        MenuItem sms = menu.add("Enviar SMS");
        Intent intentSms = new Intent(Intent.ACTION_VIEW);
        intentSms.setData(Uri.parse("sms:"+ alunoSelecionado.getTelefone()));
        intentSms.putExtra("sms_body", "Mensagem");
        sms.setIntent(intentSms);

        Intent intentMapa = new Intent(Intent.ACTION_VIEW);
        intentMapa.setData(Uri.parse("geo:0,0?z=14&q=" + Uri.encode(alunoSelecionado.getEndereco())));
        MenuItem mapa = menu.add("Achar no Mapa");
        mapa.setIntent(intentMapa);

        Intent intentSite = new Intent(Intent.ACTION_VIEW);
        String site = alunoSelecionado.getSite();
        if (!site.startsWith("http://"))
            site = "http://" + site;
        intentSite.setData(Uri.parse(site));
        MenuItem itemSite = menu.add("Navegar no site");
        itemSite.setIntent(intentSite);

        MenuItem deletar = menu.add("Deletar");
        deletar.setOnMenuItemClickListener(new MenuItem.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(MenuItem item) {
                new AlertDialog.Builder(ListaAlunosActivity.this)
                        .setIcon(android.R.drawable.ic_dialog_alert)
                        .setTitle("Deletar")
                        .setMessage("Deseja mesmo deletar ?")
                        .setPositiveButton("Quero",
                                new DialogInterface.OnClickListener() {
                                    public void onClick(DialogInterface dialog, int which) {
                                        AlunoDAO dao = new AlunoDAO(ListaAlunosActivity.this);
                                        dao.deletar(alunoSelecionado);
                                        dao.close();
                                        carregaLista();
                                    }
                                }).setNegativeButton("Nao", null).show();

                return false;
            }
        });

        this.carregaLista();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_lista_alunos, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        switch (id){
            case R.id.menu_enviar_notas:
                new EnviaContatosTask(this).execute();
                return true;
            case R.id.menu_receber_provas:
                Intent provas = new Intent(this, ProvasActivity.class);
                startActivity(provas);
                return true;
            case R.id.menu_mapa:
                Intent mapa = new Intent(this, MostraAlunosActivity.class);
                startActivity(mapa);
                return true;
        }

        return super.onOptionsItemSelected(item);
    }


}
