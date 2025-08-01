package com.side.onnuri.accounting.application.service.matching;

import lombok.Getter;
import lombok.Setter;

import java.util.*;

@Getter
@Setter
public class TrieNode {
    
    private Map<Character, TrieNode> children;
    private boolean isEndOfWord;
    private List<MatchingInfo> matchingInfos;
    private String keyword;
    
    @Getter
    @Setter
    public static class MatchingInfo {
        private UUID companyId;
        private UUID categoryId;
        
        public MatchingInfo(UUID companyId, UUID categoryId) {
            this.companyId = companyId;
            this.categoryId = categoryId;
        }
    }
    
    public TrieNode() {
        this.children = new HashMap<>();
        this.isEndOfWord = false;
        this.matchingInfos = new ArrayList<>();
    }
    
    public void addMatchingInfo(UUID companyId, UUID categoryId) {
        if (matchingInfos == null) {
            matchingInfos = new ArrayList<>();
        }
        matchingInfos.add(new MatchingInfo(companyId, categoryId));
    }
    
    public TrieNode getChild(char c) {
        return children.get(c);
    }
    
    public void putChild(char c, TrieNode node) {
        children.put(c, node);
    }
    
    public boolean hasChild(char c) {
        return children.containsKey(c);
    }
}