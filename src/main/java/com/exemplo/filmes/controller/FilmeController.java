package com.exemplo.filmes.controller;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.exemplo.filmes.model.Filme;
import org.springframework.web.bind.annotation.*;
import jakarta.annotation.PostConstruct;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

@RestController
@RequestMapping("/filmes")
public class FilmeController {

    private List<Filme> listaFilmes = new ArrayList<>();
    private Long proximoId = 1L;
    private static final String CAMINHO_ARQUIVO = "filmes.txt";
    private final ObjectMapper objectMapper = new ObjectMapper();

    @PostConstruct
    public void carregarDoArquivo() {
        File arquivo = new File(CAMINHO_ARQUIVO);
        if (arquivo.exists()) {
            try {
                listaFilmes = objectMapper.readValue(arquivo, new TypeReference<List<Filme>>() {});
                proximoId = listaFilmes.stream()
                        .mapToLong(f -> f.getId() != null ? f.getId() : 0L)
                        .max()
                        .orElse(0L) + 1L;

                System.out.println("Filmes carregados com sucesso do arquivo " + CAMINHO_ARQUIVO);
            } catch (IOException e) {
                System.err.println("Erro ao carregar o arquivo de filmes: " + e.getMessage());
            }
        }
    }

    private void salvarNoArquivo() throws IOException {
        objectMapper.writerWithDefaultPrettyPrinter().writeValue(new File(CAMINHO_ARQUIVO), listaFilmes);
    }

    @GetMapping
    public List<Filme> listar() {
        return listaFilmes;
    }

    @PostMapping
    public Filme cadastrar(@RequestBody Filme filme) throws IOException {
        filme.setId(proximoId++);
        listaFilmes.add(filme);
        salvarNoArquivo();

        return filme;
    }
}