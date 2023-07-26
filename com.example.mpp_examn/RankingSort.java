package com.example.mpp_examn;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

public class RankingSort implements Comparator<Player> {

    @Override
    public int compare(Player o1, Player o2) {
        return Integer.compare(o1.getScore(), o2.getScore());
    }

    public void setRankingPlayers(List<Player> players)
    {
        List<Player> sortedList = new ArrayList<>(players);

        for (int i = 0; i < sortedList.size(); ++i)
        {
            for (int j = i + 1; j < sortedList.size(); ++j)
            {
                if (compare(sortedList.get(i), sortedList.get(j)) > 0)
                {
                    Player aux = sortedList.get(i);
                    sortedList.set(i, sortedList.get(j));
                    sortedList.set(j, aux);
                }
            }
        }

        for (int i = 0; i < players.size(); ++i)
        {
            for (int j = 0; j < sortedList.size(); ++j)
            {
                if (players.get(i).equals(sortedList.get(j)))
                {
                    players.get(i).setRanking(j);
                    break;
                }
            }
        }
    }
}
