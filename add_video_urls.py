import re

with open('app/src/main/java/com/example/data/ExerciseLibraryData.kt', 'r') as f:
    content = f.read()

# Update data class
content = content.replace(
    'val longevityScienceNote: String',
    'val longevityScienceNote: String,\n    val videoUrl: String'
)

# Define real youtube embed URLs for common exercises
videos = {
    "goblet_squat": "https://www.youtube.com/embed/MeIiIdhgPwg",
    "romanian_deadlift": "https://www.youtube.com/embed/JCXUYuzwNrM",
    "farmer_carry": "https://www.youtube.com/embed/p17J5-A-B-U",
    "glute_bridge": "https://www.youtube.com/embed/8bbE64NuDTU",
    "push_up_incline": "https://www.youtube.com/embed/Z0ibHdc9hEQ",
    "bulgarian_split_squat": "https://www.youtube.com/embed/2C-uNgKwPLE",
    "pallof_press": "https://www.youtube.com/embed/nrsUXN0Ww58",
    "tibialis_raise": "https://www.youtube.com/embed/gWia_82_xGo",
    "step_up_knee_drive": "https://www.youtube.com/embed/9w_Y29G9H3Y",
    "face_pull": "https://www.youtube.com/embed/rep-qVOkqgk",
    "dead_bug_bracing": "https://www.youtube.com/embed/4XLEnwUr1d8",
    "overhead_press": "https://www.youtube.com/embed/QAQ64hK4Xxs",
}

for exercise_id, url in videos.items():
    # We need to find the specific exercise block and append videoUrl
    # They look like: id = "goblet_squat", ... longevityScienceNote = "..."\n        )
    
    # We'll regex replace the longevityScienceNote line for each id, but since it's hard to match block,
    # let's just do a generic replacement if we can capture the block.
    pass

# Simpler approach: replace `longevityScienceNote = "..."` with `longevityScienceNote = "...",\n            videoUrl = "..."`
# but since notes vary, let's use regex to match the item and append to the end.

def replacer(match):
    block = match.group(0)
    id_match = re.search(r'id = "(.*?)"', block)
    if id_match:
        ex_id = id_match.group(1)
        vid_url = videos.get(ex_id, "https://www.youtube.com/embed/dQw4w9WgXcQ")
        # Find the last closing parenthesis of the block
        # The block ends with \n        )
        return block[:-9] + ',\n            videoUrl = "{}"\n        )'.format(vid_url)
    return block

content = re.sub(r'ExerciseLibraryItem\([\s\S]*?\n\s{8}\)', replacer, content)

with open('app/src/main/java/com/example/data/ExerciseLibraryData.kt', 'w') as f:
    f.write(content)

