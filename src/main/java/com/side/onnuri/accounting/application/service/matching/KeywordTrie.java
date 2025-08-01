package com.side.onnuri.accounting.application.service.matching;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Component
@Slf4j
public class KeywordTrie {
    
    private TrieNode root;
    
    public KeywordTrie() {
        this.root = new TrieNode();
    }
    
    /**
     * Trie에 키워드 추가
     */
    public void insert(String keyword, UUID companyId, UUID categoryId) {
        if (keyword == null || keyword.trim().isEmpty()) {
            return;
        }
        
        TrieNode current = root;
        String normalizedKeyword = keyword.toLowerCase().trim();
        
        for (char c : normalizedKeyword.toCharArray()) {
            if (!current.hasChild(c)) {
                current.putChild(c, new TrieNode());
            }
            current = current.getChild(c);
        }
        
        current.setEndOfWord(true);
        current.addMatchingInfo(companyId, categoryId);
        current.setKeyword(keyword);
        
        log.debug("키워드 추가: {} -> Company: {}, Category: {}", keyword, companyId, categoryId);
    }
    
    /**
     * 문자열에서 모든 매칭되는 키워드 찾기
     */
    public List<KeywordMatch> findMatches(String text) {
        if (text == null || text.trim().isEmpty()) {
            return new ArrayList<>();
        }
        
        List<KeywordMatch> matches = new ArrayList<>();
        String normalizedText = text.toLowerCase().trim();
        
        // 모든 시작 위치에서 키워드 탐색
        for (int i = 0; i < normalizedText.length(); i++) {
            findMatchesFromPosition(normalizedText, i, matches);
        }
        
        log.debug("텍스트 '{}' 에서 {}개 키워드 매칭", text, matches.size());
        return matches;
    }
    
    private void findMatchesFromPosition(String text, int startPos, List<KeywordMatch> matches) {
        TrieNode current = root;
        
        for (int i = startPos; i < text.length(); i++) {
            char c = text.charAt(i);
            
            if (!current.hasChild(c)) {
                break;
            }
            
            current = current.getChild(c);
            
            // 완전한 키워드 발견
            if (current.isEndOfWord()) {
                // 해당 키워드에 대한 모든 매칭 정보 추가
                for (TrieNode.MatchingInfo info : current.getMatchingInfos()) {
                    KeywordMatch match = KeywordMatch.builder()
                            .keyword(current.getKeyword())
                            .companyId(info.getCompanyId())
                            .categoryId(info.getCategoryId())
                            .startPosition(startPos)
                            .endPosition(i)
                            .length(i - startPos + 1)
                            .build();
                    
                    matches.add(match);
                    log.debug("키워드 매칭: {} (위치: {}-{})", current.getKeyword(), startPos, i);
                }
            }
        }
    }
    
    /**
     * Trie 초기화
     */
    public void clear() {
        this.root = new TrieNode();
        log.info("키워드 Trie 초기화 완료");
    }
}