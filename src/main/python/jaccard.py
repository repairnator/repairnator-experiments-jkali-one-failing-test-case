class Jaccard():
    """Class containing methods for computing Jaccard similarity between two sentences based on character n-gram overlap."""

    @staticmethod
    def text2ngrams(text, n=3):
        """Convert text into character n-grams."""
        return [text[i:i+n] for i in range(len(text)-n+1)]

    @staticmethod
    def jaccard_overlap(text1, text2):
        """
        Converts the text to character n-gram set and computes Jaccard overlap.
        Calculates Jaccard overlap scores between two texts.
        """
        set1 = set(Jaccard.text2ngrams(text1, n=3))
        set2 = set(Jaccard.text2ngrams(text2, n=3))
        intersection = set1.intersection(set2)
        union = set1.union(set2)
        return (len(intersection) / float(len(union)))

    @staticmethod
    def most_similar_passage(query, candidate_passages):
        """Returns the passage with the highest Jaccard overlap with the query."""
        best_sim = -1
        best_passage = ""
        for passage in candidate_passages:
            sim = Jaccard.jaccard_overlap(query, passage)
            if sim > best_sim:
                best_sim = sim
                best_passage = passage
        return best_passage

    @staticmethod
    def score(query, candidate_passages, sort_by_score=True):
        """Score passages using Jaccard similarity."""
        scored_passages = [{'passage': p, 'score': Jaccard.jaccard_overlap(query, p)} for p in candidate_passages]
        if sort_by_score:
            return sorted(scored_passages, key=lambda d: d['score'], reverse=True)
        return scored_passages


if __name__ == "__main__":
    """Test out the Jaccard class."""
    t1 = "hello, how are you?"
    t2 = "hello, howe are you Brian?"
    t3 = "hi how have you been?"
    print("Text 1: {}".format(t1))
    print("Text 2: {}".format(t2))
    print("Text 3: {}\n".format(t3))

    jaccard = Jaccard()
    print("Jaccard overlap between texts 1 and 2: {}".format(jaccard.jaccard_overlap(t1, t2)))
    print("Jaccard overlap between texts 1 and 3: {}".format(jaccard.jaccard_overlap(t1, t3)))
    print("Most similar passage to text 1 (query): {}".format(jaccard.most_similar_passage(t1, [t2, t3])))
