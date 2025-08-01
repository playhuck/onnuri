package com.side.onnuri.accounting.application.service.matching;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.List;
import java.util.UUID;

@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class MatchingResult {
    
    private UUID companyId;
    private String companyName;
    private UUID categoryId;
    private String categoryName;
    
    private int matchingScore;              // 매칭 점수
    private int priority;                   // 우선순위
    private List<String> matchedKeywords;   // 매칭된 키워드들
    private boolean amountMatched;          // 금액 조건 매칭 여부
    private double relevanceScore;          // 적합도 점수 (0.0 ~ 1.0)
    
    /**
     * 다른 매칭 결과와 비교
     * 1. 매칭 점수가 높을수록 우선
     * 2. 매칭 점수가 같으면 우선순위가 높을수록 우선
     * 3. 둘 다 같으면 적합도가 높을수록 우선
     */
    public int compareTo(MatchingResult other) {
        // 1. 매칭 점수 비교
        int scoreComparison = Integer.compare(other.matchingScore, this.matchingScore);
        if (scoreComparison != 0) {
            return scoreComparison;
        }
        
        // 2. 우선순위 비교
        int priorityComparison = Integer.compare(other.priority, this.priority);
        if (priorityComparison != 0) {
            return priorityComparison;
        }
        
        // 3. 적합도 비교
        return Double.compare(other.relevanceScore, this.relevanceScore);
    }
    
    /**
     * 동일한 점수와 우선순위를 가지는지 확인
     */
    public boolean hasEqualScoreAndPriority(MatchingResult other) {
        return this.matchingScore == other.matchingScore && 
               this.priority == other.priority;
    }
}